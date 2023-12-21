package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Hotel;

import java.time.Instant;
import java.util.List;

public interface HotelSupplier {
    List<Hotel> getHotel(String hotelName, String checkInDate, String checkOutDate, String location, Instant ts);
}
