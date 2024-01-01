package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class VacationVisionaryListener implements MessageListener {

    private final SQLiteEventStore eventStore;
    private final String topic;

    public VacationVisionaryListener(SQLiteEventStore eventStore, String topic) {
        this.eventStore = eventStore;
        this.topic = topic;
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage textMessage) {
            String json;
            try {
                json = textMessage.getText();
                processMessage(json);
            } catch (Exception e) {
                // Manejo de excepciones al procesar el mensaje
                System.err.println("Error al procesar el mensaje: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void processMessage(String json) {
        try {
            switch (topic) {
                case "prediction.weather":
                    eventStore.insertWeather(json);
                    break;
                case "hotel.rates":
                    SQLiteEventStore.insertHotel(json);
                    SQLiteEventStore.insertCheckInOut(json);
                    SQLiteEventStore.insertHotelRates(json);
                    break;
                default:
                    System.out.println("Unhandled topic: " + topic);
            }
        } catch (Exception e) {
            // Manejo de excepciones al procesar el mensaje
            System.err.println("Error al procesar el mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }
}