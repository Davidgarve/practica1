package org.ulpc.dacd.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class OpenWeatherMapSupplier {

    public List<Weather> getWeather(Location location, Instant ts) {
        try {
            ZonedDateTime zonedDateTime = ts.atZone(ZoneId.of("Atlantic/Canary"));
            ZonedDateTime date12PM = zonedDateTime.with(LocalTime.of(12, 0, 0));

            String apiUrl = "https://api.openweathermap.org/data/2.5/forecast" +
                    "?lat=" + location.getLat() +
                    "&lon=" + location.getLon() +
                    "&appid=d41a06840247e2449a1ddc74dd6789da";

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
                List<Weather> dailyWeatherForecast = parseDailyWeatherData(responseData, 5);

                return dailyWeatherForecast;
            } else {
                System.err.println("Error " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Weather> parseDailyWeatherData(String responseData, int numDays) {
        List<Weather> dailyWeatherForecast = new ArrayList<>();
        JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();

        if (jsonObject.has("list")) {
            JsonArray dailyForecastList = jsonObject.getAsJsonArray("list");

            for (int i = 0; i < numDays; i++) {
                JsonObject dailyData = dailyForecastList.get(i).getAsJsonObject();

                String dtTxt = dailyData.get("dt_txt").getAsString();
                int cloudsAll = dailyData.getAsJsonObject("clouds").get("all").getAsInt();
                double pop = dailyData.get("pop").getAsDouble();
                double temp = dailyData.getAsJsonObject("main").get("temp").getAsDouble();
                int humidity = dailyData.getAsJsonObject("main").get("humidity").getAsInt();
                double speed = dailyData.getAsJsonObject("wind").get("speed").getAsDouble();

                Weather dailyWeather = new Weather();
                dailyWeather.setDtTxt(dtTxt);
                dailyWeather.setPop(pop);
                dailyWeather.setTemp(temp);
                dailyWeather.setHumidity(humidity);
                dailyWeather.setSpeed(speed);
                dailyWeather.setCloudsAll(cloudsAll);

                dailyWeatherForecast.add(dailyWeather);
            }
        }

        return dailyWeatherForecast;
    }
}
