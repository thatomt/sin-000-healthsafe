package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.mq.MqConfig;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

/** Broadcasts staffing updates on the topic. Fire-and-forget: nothing is stored for absent listeners. */
final class StaffingEventPublisher {

    private final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

    void publish(String json) throws JMSException {
        Connection connection = factory.createConnection();
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(session.createTopic(MqConfig.TOPIC));
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.send(session.createTextMessage(json));
        } finally {
            connection.close();
        }
    }
}