package org.ulpc.dacd.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpc.dacd.model.Location;
import org.ulpc.dacd.model.Weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OpenWeatherMapSupplier {

    public List<Weather> getWeather(Location location, Instant ts) {
        try {
            String apiUrl = "https://api.openweathermap.org/data/2.5/forecast" +
                    "?lat=" + location.getLat() +
                    "&lon=" + location.getLon() +
                    "&appid=d41a06840247e2449a1ddc74dd6789da" +
                    "&units=metric";

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String responseData = response.toString();

                return parseDailyWeatherData(responseData, ts, location);
            } else {
                System.err.println("Error " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Weather> parseDailyWeatherData(String responseData, Instant ts, Location location) {
        List<Weather> dailyWeatherForecast = new ArrayList<>();
        JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();

        if (jsonObject.has("list")) {
            JsonArray dailyForecastList = jsonObject.getAsJsonArray("list");

            for (int i = 0; i < dailyForecastList.size(); i++) {
                JsonObject dailyData = dailyForecastList.get(i).getAsJsonObject();

                String date = dailyData.get("dt_txt").getAsString();

                if (is12PM(date)) {
                    int cloudsAll = dailyData.getAsJsonObject("clouds").get("all").getAsInt();
                    double pop = dailyData.get("pop").getAsDouble();
                    double temp = dailyData.getAsJsonObject("main").get("temp").getAsDouble();
                    int humidity = dailyData.getAsJsonObject("main").get("humidity").getAsInt();
                    double speed = dailyData.getAsJsonObject("wind").get("speed").getAsDouble();

                    Weather dailyWeather = new Weather();
                    dailyWeather.setDate(date);
                    dailyWeather.setPop(pop);
                    dailyWeather.setTemp(temp);
                    dailyWeather.setHumidity(humidity);
                    dailyWeather.setSpeed(speed);
                    dailyWeather.setClouds(cloudsAll);
                    dailyWeather.setTs(ts);
                    dailyWeather.setLocation(location);
                    System.out.println(dailyWeather);
                    dailyWeatherForecast.add(dailyWeather);
                }
            }
        }

        return dailyWeatherForecast;
    }



    private boolean is12PM(String date) {
        return date.endsWith(" 12:00:00");
    }
}
