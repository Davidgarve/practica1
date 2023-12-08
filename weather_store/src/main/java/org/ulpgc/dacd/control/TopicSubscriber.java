package org.ulpgc.dacd.control;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

    public class TopicSubscriber implements Subscriber{

        public void start() {
            String brokerURL = "tcp://localhost:61616";
            String topicName = "prediction.weather";
            String subscriberId = "weather_store";

            try {
                ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
                Connection connection = connectionFactory.createConnection();
                connection.setClientID(subscriberId);

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Topic topic = session.createTopic(topicName);
                javax.jms.TopicSubscriber subscriber = session.createDurableSubscriber(topic, subscriberId);
                connection.start();

                while (true) {
                    Message message = subscriber.receive();
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println("Received message: " + textMessage.getText());
                        storeEvent(textMessage.getText());

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static void storeEvent(String jsonWeather) {
            EventStore eventStore = new EventStore();
            eventStore.storeEvent(jsonWeather);
        }
    }

