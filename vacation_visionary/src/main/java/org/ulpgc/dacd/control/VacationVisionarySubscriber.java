package org.ulpgc.dacd.control;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class VacationVisionarySubscriber implements TopicSubscriber {

    public void start() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic weatherTopic = session.createTopic("prediction.weather");
            Topic hotelRateTopic = session.createTopic("hotel.rates");

            MessageConsumer weatherConsumer = session.createConsumer(weatherTopic);
            MessageConsumer hotelRatesConsumer = session.createConsumer(hotelRateTopic);

            SQLiteEventRepository eventStore = new SQLiteEventRepository();

            VacationVisionaryController weatherListener = new VacationVisionaryController(eventStore, "prediction.weather");
            VacationVisionaryController hotelRateListener = new VacationVisionaryController(eventStore, "hotel.rates");

            weatherConsumer.setMessageListener(weatherListener);
            hotelRatesConsumer.setMessageListener(hotelRateListener);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

