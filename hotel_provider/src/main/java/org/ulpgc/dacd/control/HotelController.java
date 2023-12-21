package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Hotel;

import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HotelController {
    private final XoteloSupplier xoteloAPISupplier;
    private final JmsHotelStore jmsHotelStore;
    private final String brokerURL;
    private final String topicName;

    public HotelController(XoteloSupplier xoteloAPISupplier, JmsHotelStore jmsHotelStore, String brokerURL, String topicName) {
        this.xoteloAPISupplier = xoteloAPISupplier;
        this.jmsHotelStore = jmsHotelStore;
        this.brokerURL = brokerURL;
        this.topicName = topicName;
    }

    public void execute(String hotelName, String checkInDate, String checkOutDate, String location, Instant ts) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                List<Hotel> hotelList = xoteloAPISupplier.getHotel(hotelName, checkInDate, checkOutDate, location, ts);
                for (Hotel hotel : hotelList) {
                    jmsHotelStore.sendHotelToBroker(hotel, brokerURL, topicName);
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 6 * 60 * 60 * 1000);
    }
}