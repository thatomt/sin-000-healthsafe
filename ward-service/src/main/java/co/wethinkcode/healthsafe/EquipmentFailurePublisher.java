package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.mq.MqConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

/** Puts equipment failure alerts on the persistent queue. */
final class EquipmentFailurePublisher {

    private final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
    private final ObjectMapper mapper = new ObjectMapper();

    /** Returns once the broker has stored the message. Throws JMSException if the broker is unreachable. */
    void publish(EquipmentFailureAlert alert) throws JMSException {
        String json;
        try {
            json = mapper.writeValueAsString(alert);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("could not turn the alert into JSON", e);
        }

        Connection connection = factory.createConnection();
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(session.createQueue(MqConfig.QUEUE));
            producer.setDeliveryMode(DeliveryMode.PERSISTENT); // written to disk by the broker
            producer.send(session.createTextMessage(json));
        } finally {
            connection.close();
        }
    }
}