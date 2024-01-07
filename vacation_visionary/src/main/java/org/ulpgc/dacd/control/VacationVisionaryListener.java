package org.ulpgc.dacd.control;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.CompletableFuture;

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
                System.out.println("Event received: " + json);
                processMessageAsync(json);
            } catch (Exception e) {
                System.err.println("Error processing the message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void processMessageAsync(String json) {
        CompletableFuture.runAsync(() -> processMessage(json))
                .exceptionally(ex -> {
                    System.err.println("Error processing the message asynchronously: " + ex.getMessage());
                    ex.printStackTrace();
                    return null;
                });
    }

    private void processMessage(String json) {
        try {
            switch (topic) {
                case "prediction.weather":
                    eventStore.insertWeather(json);
                    break;
                case "hotel.rates":
                    eventStore.insertHotel(json);
                    eventStore.insertCheckInOut(json);
                    eventStore.insertHotelRates(json);
                    break;
                default:
                    System.out.println("Unhandled topic: " + topic);
            }
        } catch (Exception e) {
            System.err.println("Error processing the message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
