package org.ulpgc.dacd.control;

import java.time.Instant;

public class Main {

    public static void main(String[] args) {
        String apiKey = "g658907-d237059";
        XoteloSupplier xoteloAPISupplier = new XoteloSupplier(apiKey);

        String checkInDate = "2023-12-21";
        String checkOutDate = "2023-12-25";

        String brokerURL = "tcp://localhost:61616";
        String topicName = "prediction.hotels";

        JmsHotelStore jmsHotelStore = new JmsHotelStore();
        HotelController hotelController = new HotelController(xoteloAPISupplier, jmsHotelStore, brokerURL, topicName);
        hotelController.execute("barcelo Castillo", checkInDate, checkOutDate, "Fuerteventura", Instant.now());
    }
}
