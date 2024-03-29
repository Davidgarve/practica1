package org.ulpgc.dacd.control;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.CompletableFuture;

public class VacationVisionaryController implements MessageListener {

    private final SQLiteEventRepository eventStore;
    private final String topic;

    public VacationVisionaryController(SQLiteEventRepository eventStore, String topic) {
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
        CompletableFuture.runAsync(() -> {
            synchronized (SQLiteEventRepository.class) {
                processMessage(json);
            }
        }).exceptionally(ex -> {
            System.err.println("Error processing the message asynchronously: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });
    }


    private void processMessage(String json) {
        try {
            switch (topic) {
                case "prediction.weather":
                    eventStore.saveWeather(json);
                    break;
                case "hotel.rates":
                    eventStore.saveHotel(json);
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
