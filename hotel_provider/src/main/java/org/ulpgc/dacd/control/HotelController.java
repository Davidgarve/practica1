package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Hotel;

import java.time.*;
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
                ZoneId utcZone = ZoneId.of("UTC");
                Instant currentTime = Instant.now();
                ZonedDateTime currentUtcTime = ZonedDateTime.ofInstant(currentTime, utcZone);

                ZoneId canaryIslandsZone = ZoneId.of("Atlantic/Canary");
                ZonedDateTime apiUpdateTime = ZonedDateTime.of(currentUtcTime.toLocalDateTime().toLocalDate(), LocalTime.of(17, 0), canaryIslandsZone);

                if (currentUtcTime.isBefore(apiUpdateTime)) {
                    List<Hotel> hotelList = xoteloAPISupplier.getHotel(hotelName, checkInDate, checkOutDate, location, ts);
                    for (Hotel hotel : hotelList) {
                        jmsHotelStore.sendHotelToBroker(hotel, brokerURL, topicName);
                    }
                } else {
                    LocalDate nextDay = LocalDate.parse(checkInDate).plusDays(1);
                    String nextDayString = nextDay.toString();

                    List<Hotel> hotelList = xoteloAPISupplier.getHotel(hotelName, nextDayString, checkOutDate, location, ts);
                    for (Hotel hotel : hotelList) {
                        jmsHotelStore.sendHotelToBroker(hotel, brokerURL, topicName);
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 6 * 60 * 60 * 1000);
    }
}
