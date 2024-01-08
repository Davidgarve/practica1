package org.ulpgc.dacd.control;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface WeatherBusinessLogicInterface {
    String compareWeatherConditions(String location, String formattedDate);

    boolean isDataAvailableForDates(LocalDate checkInDate, LocalDate checkOutDate);

    List<Map<String, Object>> getWeatherInfoInRange(String selectedLocation, LocalDate checkInDate, LocalDate checkOutDate);

    Map<String, String> getWeatherStatusForDate(List<Map<String, Object>> weatherData);

    String generateWeatherRecommendation(List<Map<String, String>> weatherStatusList);
}
