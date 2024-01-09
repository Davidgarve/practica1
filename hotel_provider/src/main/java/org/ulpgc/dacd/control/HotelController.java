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
    private final long refreshFrequency;

    public HotelController(XoteloSupplier xoteloAPISupplier, JmsHotelStore jmsHotelStore, String brokerURL, String topicName, long refreshFrequency) {
        this.xoteloAPISupplier = xoteloAPISupplier;
        this.jmsHotelStore = jmsHotelStore;
        this.brokerURL = brokerURL;
        this.topicName = topicName;
        this.refreshFrequency = refreshFrequency;
    }

    public void execute(String hotelName, String location) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                String checkIn = Instant.now().toString().substring(0, 10);
                Instant checkOutInstant = Instant.now().plusMillis(5 * 24 * 60 * 60 * 1000);
                String checkOut = checkOutInstant.toString().substring(0, 10);
                Instant ts = Instant.now();

                ZoneId utcZone = ZoneId.of("UTC");
                Instant currentTime = Instant.now();
                ZonedDateTime currentUtcTime = ZonedDateTime.ofInstant(currentTime, utcZone);

                ZoneId canaryIslandsZone = ZoneId.of("Atlantic/Canary");
                ZonedDateTime apiUpdateTime = ZonedDateTime.of(currentUtcTime.toLocalDateTime().toLocalDate(), LocalTime.of(17, 0), canaryIslandsZone);

                if (currentUtcTime.isAfter(apiUpdateTime)) {
                    LocalDate nextDay = LocalDate.parse(checkIn).plusDays(1);
                    checkIn = nextDay.toString();
                }

                List<Hotel> hotelList = xoteloAPISupplier.getHotel(hotelName, checkIn, checkOut, location, ts);
                for (Hotel hotel : hotelList) {
                    jmsHotelStore.sendHotelToBroker(hotel, brokerURL, topicName);
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, refreshFrequency);
    }

}
