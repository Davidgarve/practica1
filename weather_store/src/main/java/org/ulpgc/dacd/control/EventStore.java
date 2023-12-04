package org.ulpgc.dacd.control;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class EventStore {
    private static final String BASE_DIRECTORY = "eventstore";
    private static final String EVENT_DIRECTORY_PREFIX = "prediction.Weather";
    private static final String EVENT_FILE_EXTENSION = ".events";

    public static void runEventStore() {
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
            consumer.setMessageListener(new WeatherMessageListener());

            // Espera indefinidamente (puedes ajustar según tus necesidades)
            System.out.println("Event Store esperando mensajes. Presiona Ctrl+C para salir.");
            Thread.sleep(Long.MAX_VALUE);

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class WeatherMessageListener implements MessageListener {
        @Override
        public void onMessage(Message message) {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    String jsonWeather = textMessage.getText();
                    storeEvent(jsonWeather);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }

        private void storeEvent(String jsonWeather) {
            try {
                String eventDirectoryName = BASE_DIRECTORY + File.separator +
                        EVENT_DIRECTORY_PREFIX + File.separator +
                        "OpenWeatherMap" + File.separator +
                        formatDate(Instant.now());

                String eventDirectoryPath = eventDirectoryName + File.separator;
                String eventFilePath = eventDirectoryPath + "events" + EVENT_FILE_EXTENSION;

                File directory = new File(eventDirectoryName);
                File eventFile = new File(eventFilePath);

                // Crea el directorio si no existe
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Crea el archivo si no existe
                if (!eventFile.exists()) {
                    eventFile.createNewFile();
                }

                // Abre el archivo en modo de anexar (append) y escribe el evento
                try (FileWriter writer = new FileWriter(eventFile, true)) {
                    // Agrega una coma y un salto de línea si el archivo ya contiene eventos
                    if (eventFile.length() > 0) {
                        writer.write(",\n");
                    }

                    writer.write(jsonWeather);
                }

                System.out.println("Evento almacenado en: " + eventFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String formatDate(Instant ts) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            return dateFormat.format(Date.from(ts));
        }
    }
}

