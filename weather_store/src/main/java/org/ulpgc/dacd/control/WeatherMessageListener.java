package org.ulpgc.dacd.control;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class WeatherMessageListener implements MessageListener {
    private final EventStore eventStorage;

    public WeatherMessageListener(EventStore eventStorage) {
        this.eventStorage = eventStorage;
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                String jsonWeather = textMessage.getText();
                eventStorage.storeEvent(jsonWeather);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
