package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Hotel;

public interface HotelEventPublisher {
    void sendHotelToBroker(Hotel hotel, String brokerURL, String topicName);
}
