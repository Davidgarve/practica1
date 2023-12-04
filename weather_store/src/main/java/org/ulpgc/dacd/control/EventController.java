package org.ulpgc.dacd.control;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class EventController {

    public static void execute() {
        String brokerURL = "tcp://localhost:61616";
        String topicName = "prediction.weather";

        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic(topicName);

            MessageConsumer consumer = session.createConsumer(topic);

            // Configura un MessageListener para recibir mensajes de forma asíncrona
            EventStore eventStore = new EventStore();
            consumer.setMessageListener(new WeatherMessageListener(eventStore));

            System.out.println("Event Store esperando mensajes. Presiona Ctrl+C para salir.");
            Thread.sleep(Long.MAX_VALUE);

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

