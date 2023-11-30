package org.ulpc.dacd.control;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpc.dacd.model.Location;
import org.ulpc.dacd.model.Weather;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.time.Instant;
import java.util.List;

public class JmsWeatherStore {
    public static void main(String[] args) {
        Location arrecife = new Location(28.966271, -13.545968, "Arrecife");
        // Configura la conexión con ActiveMQ
        String brokerURL = "tcp://localhost:8161"; // Ajusta según tu configuración
        String queueName = "prediction.weather"; // Ajusta el nombre de la cola

        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Crea una sesión sin transacciones y con envío automático de acuse de recibo
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Crea la cola (si no existe)
            // En un entorno de producción, la creación de la cola podría hacerse fuera de la aplicación
            // y ser administrada por un administrador de colas
            session.createQueue(queueName);

            // Crea un productor para la cola
            MessageProducer producer = session.createProducer(session.createQueue(queueName));

            // Obtiene los datos del tiempo utilizando OpenWeatherMapSupplier
            OpenWeatherMapSupplier weatherSupplier = new OpenWeatherMapSupplier("d41a06840247e2449a1ddc74dd6789da");
            List<Weather> weatherData = weatherSupplier.getWeather(arrecife, Instant.now());

            // Envía los mensajes con los datos del tiempo
            for (Weather weather : weatherData) {
                TextMessage message = session.createTextMessage(convertWeatherToJson(weather));
                producer.send(message);
            }

            // Cierra la conexión
            producer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String convertWeatherToJson(Weather weather) {
        Gson gson = new Gson();
        return gson.toJson(weather);
    }

}
