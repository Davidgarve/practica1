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

        StringBuilder advice = new StringBuilder("Condiciones meteorológicas para la ubicación " + selectedLocation + " el " + selectedDate + ":\n");

        for (String location : locations) {
            Map<String, Object> weatherMap = getWeatherInfo(location, selectedDate);

            if (weatherMap != null) {
                String weatherDescription = getWeatherDescription(weatherMap);
                // Eliminar etiquetas HTML
                weatherDescription = weatherDescription.replaceAll("<[^>]*>", "");
                advice.append(weatherDescription).append("\n");
            } else {
                advice.append("No se encontró información meteorológica.\n");
            }
        }

        return advice.toString();
    }

    private String getWeatherDescription(Map<String, Object> weatherMap) {
        return String.format(
                "Temperatura: %.2f°C, Viento: %.2f m/s, Humedad: %d%%, Nubosidad: %d%%, Probabilidad de Precipitaciones: %.2f%%",
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
        if (temperature > 25.0) {
            return "high";
        } else if (temperature >= 15.0 && temperature <= 25.0) {
            return "moderate";
        } else {
            return "low";
        }
    }

    private String classifyPrecipitation(double precipitationProbability) {
        if (precipitationProbability < 0.3) {
            return "low";
        } else {
            return "high";
        }
    }

    private String classifyClouds(int clouds) {
        if (clouds > 70) {
            return "high";
        } else if (clouds >= 30 && clouds <= 70) {
            return "moderate";
        } else {
            return "low";
        }
    }

    private String classifyWind(double windSpeed) {
        if (windSpeed >= 5.0) {
            return "high";
        } else {
            return "low";
        }
    }

    public String generateWeatherRecommendation(List<Map<String, String>> weatherStatusList) {
        StringBuilder recommendation = new StringBuilder("Recomendación de viaje:\n");

        boolean highTemperature = weatherStatusList.stream()
                .allMatch(status -> "high".equals(status.get("temperature")));

        boolean moderateTemperature = weatherStatusList.stream()
                .allMatch(status -> "moderate".equals(status.get("temperature")));

        boolean lowTemperature = weatherStatusList.stream()
                .allMatch(status -> "low".equals(status.get("temperature")));

        boolean lowPrecipitation = weatherStatusList.stream()
                .allMatch(status -> "low".equals(status.get("precipitation")));

        boolean highPrecipitation = weatherStatusList.stream()
                .allMatch(status -> "high".equals(status.get("precipitation")));

        boolean lowClouds = weatherStatusList.stream()
                .allMatch(status -> "low".equals(status.get("clouds")));

        boolean moderateClouds = weatherStatusList.stream()
                .allMatch(status -> "moderate".equals(status.get("clouds")));

        boolean highClouds = weatherStatusList.stream()
                .allMatch(status -> "high".equals(status.get("clouds")));

        boolean highWind = weatherStatusList.stream()
                .allMatch(status -> "high".equals(status.get("wind")));

        boolean lowWind = weatherStatusList.stream()
                .allMatch(status -> "low".equals(status.get("wind")));

        if (highTemperature && lowPrecipitation) {
            recommendation.append("Le recomiendo visitar el destino seleccionado, ya que se esperan temperaturas altas y baja probabilidad de lluvia durante su estancia.\n");
        }

        if (moderateTemperature && lowPrecipitation) {
            recommendation.append("Le recomiendo visitar el destino seleccionado, ya que se esperan temperaturas moderadas y baja probabilidad de lluvia durante su estancia.\n");
        }

        if (lowTemperature && lowPrecipitation) {
            recommendation.append("No le recomiendo visitar el destino seleccionado, ya que se esperan temperaturas bajas. Aunque no se esperan precipitaciones.\n");
        }

        if (lowTemperature && highPrecipitation) {
            recommendation.append("No le recomiendo visitar el destino seleccionado, ya que se esperan temperaturas bajas y fuertes precipitaciones.\n");
        }

        if (lowClouds) {
            recommendation.append("Además, se espera que la mayoría de los días de su estancia tenga un cielo despejado.\n");
        }

        if (moderateClouds){
            recommendation.append("Además, se espera que la mayoría de los días de su estancia tenga un número moderado de nubes.\n");
        }

        if (highClouds){
            recommendation.append("Además, se espera que la mayoría de los días de su estancia tenga un cielo muy nublado.\n");
        }

        if (highWind){
            recommendation.append("Por otra parte, se esperan fuertes rachas de viento.\n");
        }

        if (lowWind){
            recommendation.append("Por otra parte, no se esperan fuertes rachas de viento.\n");
        }

        recommendation.append("Si quiere tener una predicción del tiempo más certera para un día en concreto visita la pestaña 'Predicción del Tiempo'.\n");
        return recommendation.toString();
    }

    public boolean isDataAvailableForDates(String locationName, LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate != null && checkOutDate != null) {
            // Ambas fechas están presentes, deben estar dentro del rango
            boolean isCheckInDateInRange = eventStore.getAvailableDates().contains(checkInDate.toString());
            boolean isCheckOutDateInRange = eventStore.getAvailableDates().contains(checkOutDate.toString());

            return isCheckInDateInRange && isCheckOutDateInRange;
        } else if (checkInDate != null) {
            // Solo check-in está presente, debe estar dentro del rango
            return eventStore.getAvailableDates().contains(checkInDate.toString());
        } else if (checkOutDate != null) {
            // Solo check-out está presente, debe estar dentro del rango
            return eventStore.getAvailableDates().contains(checkOutDate.toString());
        }

        return false; // Ninguna de las fechas está presente
    }
}