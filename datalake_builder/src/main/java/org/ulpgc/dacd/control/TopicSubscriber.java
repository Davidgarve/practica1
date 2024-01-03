package org.ulpgc.dacd.control;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class TopicSubscriber implements Subscriber {

    public void start(String baseDirectory) {
        String brokerURL = "tcp://localhost:61616";
        String ratesTopicName = "hotel.rates";
        String weatherTopicName = "prediction.weather";
        String subscriberId = "datalake_builder";

        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
            Connection connection = connectionFactory.createConnection();
            connection.setClientID(subscriberId);

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic ratesTopic = session.createTopic(ratesTopicName);
            javax.jms.TopicSubscriber ratesSubscriber = session.createDurableSubscriber(ratesTopic, subscriberId + "_rates");

            Topic weatherTopic = session.createTopic(weatherTopicName);
            javax.jms.TopicSubscriber weatherSubscriber = session.createDurableSubscriber(weatherTopic, subscriberId + "_weather");

            connection.start();

            EventStore eventStore = new EventStore(baseDirectory);
            System.out.println("Waiting for messages...");

            MessageListener ratesListener = message -> processMessage(message, eventStore, ratesTopicName);
            ratesSubscriber.setMessageListener(ratesListener);

            MessageListener weatherListener = message -> processMessage(message, eventStore, weatherTopicName);
            weatherSubscriber.setMessageListener(weatherListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processMessage(Message message, EventStore eventStore, String topic) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                System.out.println("Received message: " + textMessage.getText());
                eventStore.storeEvent(textMessage.getText(), topic);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
