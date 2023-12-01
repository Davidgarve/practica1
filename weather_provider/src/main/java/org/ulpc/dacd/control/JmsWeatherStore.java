package org.ulpc.dacd.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpc.dacd.model.Weather;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.IOException;
import java.time.Instant;

public class JmsWeatherStore {
    private static final Gson gson = prepareGson();

    public void sendWeatherToBroker(Weather weather, String brokerURL, String queueName) {
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

            // Envía el mensaje con los datos del tiempo
            TextMessage message = session.createTextMessage(gson.toJson(weather));
            producer.send(message);

            // Cierra la conexión
            producer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Gson prepareGson() {
        return new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Instant.class, new TypeAdapter<Instant>() {
            @Override
            public void write(JsonWriter out, Instant value) throws IOException {
                out.value(value.toString());
            }

            @Override
            public Instant read(JsonReader in) throws IOException {
                return Instant.parse(in.nextString());
            }
        }).create();
    }
}
