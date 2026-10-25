package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.mq.MqConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/** Listens to the staffing-events topic and records each update on the board. */
final class StaffingEventSubscriber {

    private static final long RETRY_DELAY_MS = 3000;

    private final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
    private final ObjectMapper mapper = new ObjectMapper();
    private final StaffingBoard board;

    StaffingEventSubscriber(StaffingBoard board) {
        this.board = board;
    }

    /** Connects in the background and keeps reconnecting if the broker goes away. */
    void start() {
        Thread thread = new Thread(this::runForever, "staffing-event-subscriber");
        thread.setDaemon(true);
        thread.start();
    }

    private void runForever() {
        try {
            while (true) {
                try {
                    listenUntilConnectionFails();
                    System.err.println("Lost connection to the broker, reconnecting...");
                } catch (JMSException e) {
                    System.err.println("Broker not available (" + e.getMessage() + "), will retry");
                }
                Thread.sleep(RETRY_DELAY_MS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void listenUntilConnectionFails() throws JMSException, InterruptedException {
        Connection connection = factory.createConnection();
        try {
            CountDownLatch connectionLost = new CountDownLatch(1);
            connection.setExceptionListener(e -> connectionLost.countDown());

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(session.createTopic(MqConfig.TOPIC));
            consumer.setMessageListener(this::handle);
            connection.start();
            System.out.println("Subscribed to topic " + MqConfig.TOPIC);

            connectionLost.await();
        } finally {
            try {
                connection.close();
            } catch (JMSException ignored) {
                // already broken, nothing more to do
            }
        }
    }

    private void handle(Message message) {
        if (!(message instanceof TextMessage textMessage)) {
            return;
        }
        try {
            StaffingEvent event = mapper.readValue(textMessage.getText(), StaffingEvent.class);
            board.record(event);
            System.out.println("Staffing update for " + event.wardId() + " at level " + event.alertLevel());
        } catch (IOException | JMSException e) {
            System.err.println("Ignoring unreadable staffing event: " + e.getMessage());
        }
    }
}