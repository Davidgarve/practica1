package org.ulpgc.dacd.control;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class VacationVisionaryListener implements MessageListener {

    private SQLiteEventStore eventStore;
    private String messageType;

    public VacationVisionaryListener(SQLiteEventStore eventStore, String messageType) {
        this.eventStore = eventStore;
        this.messageType = messageType;
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                String text = ((TextMessage) message).getText();
                JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();

                // Utilizar el tipo de mensaje recibido
                switch (messageType) {
                    case "prediction.weather":
                        eventStore.insertWeatherEvent(jsonObject);
                        break;
                    case "hotel.rates":
                        //eventStore.insertHotelRateEvent(jsonObject);
                        break;
                    default:
                        System.err.println("Tipo de evento desconocido: " + messageType);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Mensaje no es de tipo TextMessage");
        }
    }
}

