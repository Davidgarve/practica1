package org.ulpgc.dacd.control;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EventStore {
    void createTables();
    void saveHotel(String json);
    void saveWeather(String json);
    Map<String, Object> getWeatherInfo(String locationName, String predictionDate);
    List<String> getAvailableDates();
    List<String> getAvailableDatesInRange(LocalDate startDate, LocalDate endDate);
    List<String> getAvailableHotelLocations();
    List<Map<String, Object>> getHotelInfoForDates(String location, LocalDate checkInDate, LocalDate checkOutDate);
}
