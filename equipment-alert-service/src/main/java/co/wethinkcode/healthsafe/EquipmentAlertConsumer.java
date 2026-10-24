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

/** Consumes equipment-failure alerts from the queue with explicit acknowledgement. */
final class EquipmentAlertConsumer {

    private static final long RETRY_DELAY_MS = 3000;

    private final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
    private final ObjectMapper mapper = new ObjectMapper();
    private final AlertLog alerts;

    EquipmentAlertConsumer(AlertLog alerts) {
        this.alerts = alerts;
    }

    /** Connects in the background and keeps reconnecting if the broker goes away. */
    void start() {
        Thread thread = new Thread(this::runForever, "equipment-alert-consumer");
        thread.setDaemon(true);
        thread.start();
    }

    private void runForever() {
        try {
            while (true) {
                try {
                    consumeUntilConnectionFails();
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

    private void consumeUntilConnectionFails() throws JMSException, InterruptedException {
        Connection connection = factory.createConnection();
        try {
            CountDownLatch connectionLost = new CountDownLatch(1);
            connection.setExceptionListener(e -> connectionLost.countDown());

            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(session.createQueue(MqConfig.QUEUE));
            consumer.setMessageListener(message -> handle(session, message));
            connection.start();
            System.out.println("Listening on queue " + MqConfig.QUEUE);

            connectionLost.await();
        } finally {
            try {
                connection.close();
            } catch (JMSException ignored) {
                // already broken, nothing more to do
            }
        }
    }

    private void handle(Session session, Message message) {
        try {
            process(message);
            message.acknowledge(); // only now does the broker delete the message
        } catch (JMSException e) {
            System.err.println("Processing failed, asking the broker to redeliver: " + e.getMessage());
            try {
                session.recover();
            } catch (JMSException ignored) {
                // connection is gone; unacknowledged messages stay on the queue anyway
            }
        }
    }

    private void process(Message message) throws JMSException {
        if (!(message instanceof TextMessage textMessage)) {
            System.err.println("Discarding a message that is not text");
            return;
        }
        try {
            EquipmentFailureAlert alert = mapper.readValue(textMessage.getText(), EquipmentFailureAlert.class);
            if (alerts.add(alert)) {
                System.out.println("ALERT " + alert.alertId() + ": " + alert.equipment()
                        + " failed on ward " + alert.wardId());
            } else {
                System.out.println("Ignoring repeat delivery of alert " + alert.alertId());
            }
        } catch (IOException e) {
            // An unreadable message will never succeed, so redelivering it forever would be wrong.
            System.err.println("Discarding unreadable alert: " + e.getMessage());
        }
    }
}