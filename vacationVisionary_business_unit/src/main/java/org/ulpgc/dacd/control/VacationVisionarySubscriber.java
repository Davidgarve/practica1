package org.ulpgc.dacd.control;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class VacationVisionarySubscriber {

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

            SQLiteEventStore eventStore = new SQLiteEventStore();

            VacationVisionaryListener weatherListener = new VacationVisionaryListener(eventStore, "prediction.weather");
            VacationVisionaryListener hotelRateListener = new VacationVisionaryListener(eventStore, "hotel.rates");

            weatherConsumer.setMessageListener(weatherListener);
            hotelRatesConsumer.setMessageListener(hotelRateListener);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

