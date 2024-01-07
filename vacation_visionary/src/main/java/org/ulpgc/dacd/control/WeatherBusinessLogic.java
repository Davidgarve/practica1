package org.ulpgc.dacd.control;

import java.time.LocalDate;
import java.util.*;

public class WeatherBusinessLogic {

    private final SQLiteEventStore eventStore;

    public WeatherBusinessLogic(SQLiteEventStore eventStore) {
        this.eventStore = eventStore;
    }

    public Map<String, Object> getWeatherInfo(String locationName, String predictionDate) {
        return eventStore.getWeatherInfo(locationName, predictionDate);
    }

    public String compareWeatherConditions(String selectedLocation, String selectedDate) {
        List<String> locations = new ArrayList<>();
        locations.add(selectedLocation);

        StringBuilder advice = new StringBuilder("Weather conditions for location " + selectedLocation + " on " + selectedDate + ":\n");

        for (String location : locations) {
            Map<String, Object> weatherMap = getWeatherInfo(location, selectedDate);

            if (weatherMap != null) {
                String weatherDescription = getWeatherDescription(weatherMap);
                advice.append(weatherDescription).append("\n");
            } else {
                advice.append("No weather information found for this date.\n");
            }
        }

        return advice.toString();
    }

    private String getWeatherDescription(Map<String, Object> weatherMap) {
        return String.format(
                "Temperature: %.2f°C, Wind: %.2f m/s, Humidity: %d%%, Cloudiness: %d%%, Precipitation Probability: %.2f%%.",
                weatherMap.get("temp"),
                weatherMap.get("windSpeed"),
                weatherMap.get("humidity"),
                weatherMap.get("clouds"),
                weatherMap.get("pop")
        );
    }

    public List<Map<String, Object>> getWeatherInfoInRange(String locationName, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> weatherInfoList = new ArrayList<>();

        List<String> availableDates = SQLiteEventStore.getAvailableDatesInRange(startDate, endDate);

        for (String date : availableDates) {
            Map<String, Object> weatherInfo = eventStore.getWeatherInfo(locationName, date);
            if (weatherInfo != null) {
                weatherInfoList.add(weatherInfo);
            }
        }

        return weatherInfoList;
    }

    public Map<String, String> getWeatherStatusForDate(List<Map<String, Object>> weatherData) {
        Map<String, String> weatherStatus = new HashMap<>();

        for (Map<String, Object> data : weatherData) {
            double temperature = (double) data.get("temp");
            double precipitationProbability = (double) data.get("pop");
            Integer clouds = (Integer) data.get("clouds");
            double windSpeed = (double) data.get("windSpeed");

            String temperatureCategory = classifyTemperature(temperature);
            String precipitationCategory = classifyPrecipitation(precipitationProbability);
            String cloudsCategory = classifyClouds(clouds);
            String windCategory = classifyWind(windSpeed);

            weatherStatus.put("temperature", temperatureCategory);
            weatherStatus.put("precipitation", precipitationCategory);
            weatherStatus.put("clouds", cloudsCategory);
            weatherStatus.put("wind", windCategory);
        }

        return weatherStatus;
    }

    private String classifyTemperature(double temperature) {
        return (temperature > 25.0) ? "high" : (temperature >= 15.0 && temperature <= 25.0) ? "moderate" : "low";
    }

    private String classifyPrecipitation(double precipitationProbability) {
        return (precipitationProbability < 0.3) ? "low" : "high";
    }

    private String classifyClouds(int clouds) {
        return (clouds > 70) ? "high" : (clouds >= 30 && clouds <= 70) ? "moderate" : "low";
    }

    private String classifyWind(double windSpeed) {
        return (windSpeed >= 5.0) ? "high" : "low";
    }

    public String generateWeatherRecommendation(List<Map<String, String>> weatherStatusList) {
        StringBuilder recommendation = new StringBuilder("Travel recommendation:\n");

        boolean highTemperature = weatherStatusList.stream().allMatch(status -> "high".equals(status.get("temperature")));
        boolean moderateTemperature = weatherStatusList.stream().allMatch(status -> "moderate".equals(status.get("temperature")));
        boolean lowTemperature = weatherStatusList.stream().allMatch(status -> "low".equals(status.get("temperature")));
        boolean lowPrecipitation = weatherStatusList.stream().allMatch(status -> "low".equals(status.get("precipitation")));
        boolean highPrecipitation = weatherStatusList.stream().allMatch(status -> "high".equals(status.get("precipitation")));
        boolean lowClouds = weatherStatusList.stream().allMatch(status -> "low".equals(status.get("clouds")));
        boolean moderateClouds = weatherStatusList.stream().allMatch(status -> "moderate".equals(status.get("clouds")));
        boolean highClouds = weatherStatusList.stream().allMatch(status -> "high".equals(status.get("clouds")));
        boolean highWind = weatherStatusList.stream().allMatch(status -> "high".equals(status.get("wind")));
        boolean lowWind = weatherStatusList.stream().allMatch(status -> "low".equals(status.get("wind")));

        if (highTemperature && lowPrecipitation) {
            recommendation.append("I recommend visiting the selected destination as high temperatures and low precipitation are expected during your stay.\n");
        }

        if (moderateTemperature && lowPrecipitation) {
            recommendation.append("I recommend visiting the selected destination as moderate temperatures and low precipitation are expected during your stay.\n");
        }

        if (lowTemperature && lowPrecipitation) {
            recommendation.append("I do not recommend visiting the selected destination as low temperatures are expected, although no precipitation is expected.\n");
        }

        if (lowTemperature && highPrecipitation) {
            recommendation.append("I do not recommend visiting the selected destination as low temperatures and heavy precipitation are expected.\n");
        }

        if (lowClouds) {
            recommendation.append("Additionally, most days of your stay are expected to have clear skies.\n");
        }

        if (moderateClouds){
            recommendation.append("Additionally, most days of your stay are expected to have a moderate number of clouds.\n");
        }

        if (highClouds){
            recommendation.append("Additionally, most days of your stay are expected to have a very cloudy sky.\n");
        }

        if (highWind){
            recommendation.append("Furthermore, strong wind gusts are expected.\n");
        }

        if (lowWind){
            recommendation.append("Furthermore, no strong wind gusts are expected.\n");
        }

        recommendation.append("If you want a more accurate weather prediction for a specific day, visit the 'Weather Forecast' tab.\n");
        return recommendation.toString();
    }

    public boolean isDataAvailableForDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate != null && checkOutDate != null) {
            boolean isCheckInDateInRange = eventStore.getAvailableDates().contains(checkInDate.toString());
            boolean isCheckOutDateInRange = eventStore.getAvailableDates().contains(checkOutDate.toString());

            return isCheckInDateInRange && isCheckOutDateInRange;
        } else if (checkInDate != null) {
            return eventStore.getAvailableDates().contains(checkInDate.toString());
        } else if (checkOutDate != null) {
            return eventStore.getAvailableDates().contains(checkOutDate.toString());
        }

        return false;
    }
}
