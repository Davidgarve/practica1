package org.ulpgc.dacd.control;

import java.time.LocalDate;
import java.util.*;

public class WeatherBusinessLogic implements WeatherBusinessLogicInterface{

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

        List<String> availableDates = eventStore.getAvailableDatesInRange(startDate, endDate);

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

        boolean highTemperature = checkCondition(weatherStatusList, "temperature", "high");
        boolean moderateTemperature = checkCondition(weatherStatusList, "temperature", "moderate");
        boolean lowTemperature = checkCondition(weatherStatusList, "temperature", "low");
        boolean lowPrecipitation = checkCondition(weatherStatusList, "precipitation", "low");
        boolean highPrecipitation = checkCondition(weatherStatusList, "precipitation", "high");
        boolean lowClouds = checkCondition(weatherStatusList, "clouds", "low");
        boolean moderateClouds = checkCondition(weatherStatusList, "clouds", "moderate");
        boolean highClouds = checkCondition(weatherStatusList, "clouds", "high");
        boolean highWind = checkCondition(weatherStatusList, "wind", "high");
        boolean lowWind = checkCondition(weatherStatusList, "wind", "low");

        if (highTemperature && lowPrecipitation) {
            recommendation.append("I recommend visiting the selected destination as high temperatures and little precipitation are expected during most days of your stay.\n");
        }

        if (moderateTemperature && lowPrecipitation) {
            recommendation.append("I recommend visiting the selected destination as moderate temperatures and low probability of precipitation are expected during most days of your stay.\n");
        }

        if (lowTemperature && lowPrecipitation) {
            recommendation.append("I do not recommend visiting the selected destination as low temperatures are expected, although no precipitation is expected during most days of your stay.\n");
        }

        if (lowTemperature && highPrecipitation) {
            recommendation.append("I do not recommend visiting the selected destination as low temperatures and heavy precipitation are expected during most days of your stay.\n");
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

    private boolean checkCondition(List<Map<String, String>> weatherStatusList, String property, String value) {
        long count = weatherStatusList.stream()
                .filter(status -> value.equals(status.get(property))).count();
        return count > weatherStatusList.size() / 2;
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
