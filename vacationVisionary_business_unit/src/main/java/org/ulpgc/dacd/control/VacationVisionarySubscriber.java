package org.ulpgc.dacd.control;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class VacationVisionarySubscriber {

    public void start() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Crear sesión
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Crear destinatarios (colas o tópicos)
            Topic weatherTopic = session.createTopic("prediction.weather");
            Topic hotelRateTopic = session.createTopic("hotel.rates");

            // Crear consumidores
            MessageConsumer weatherConsumer = session.createConsumer(weatherTopic);
            MessageConsumer hotelRatesConsumer = session.createConsumer(hotelRateTopic);

            // Crear el objeto SQLiteEventStore
            SQLiteEventStore eventStore = new SQLiteEventStore();

            // Crear el listener y asignarle el tipo de mensaje
            VacationVisionaryListener weatherListener = new VacationVisionaryListener(eventStore, "prediction.weather");
            VacationVisionaryListener hotelRateListener = new VacationVisionaryListener(eventStore, "hotel.rates");

            // Asignar los listeners a los consumidores
            weatherConsumer.setMessageListener(weatherListener);
            hotelRatesConsumer.setMessageListener(hotelRateListener);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

