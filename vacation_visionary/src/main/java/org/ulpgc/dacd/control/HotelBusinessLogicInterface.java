package org.ulpgc.dacd.control;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HotelBusinessLogicInterface {
    List<String> getAvailableHotelLocations();

    List<Map<String, Object>> getHotelInfoForDates(String selectedLocation, LocalDate checkInDate, LocalDate checkOutDate);
}



