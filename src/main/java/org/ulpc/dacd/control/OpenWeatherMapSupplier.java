package org.ulpc.dacd.control;
import org.ulpc.dacd.model.Location;
import org.ulpc.dacd.model.Weather;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;

public class OpenWeatherMapSupplier {

    public Weather getWeather(Location location, Instant ts) {
        try {
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + location.getLat() + "&lon=" + location.getLon() + "&appid=d41a06840247e2449a1ddc74dd6789da";
            URL url = new URL(apiUrl);
            System.out.println(apiUrl);
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
                Weather weather = parseWeatherData(responseData);

                return weather; // Devuelve el objeto Weather
            } else {
                System.err.println("Error " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Weather parseWeatherData(String responseData) {
        // Parsear los datos JSON y filtrar solo los atributos necesarios
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(responseData);

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            // Filtra los atributos necesarios
            double speed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
            int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
            double temp = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
            double feelsLike = jsonObject.getAsJsonObject("main").get("feels_like").getAsDouble();


            // Crea el objeto Weather con los atributos filtrados
            Weather weather = new Weather();
            weather.setSpeed(speed);
            weather.setHumidity(humidity);
            weather.setTemp(temp);
            weather.setFeelsLike(feelsLike);

            return weather;
        }

        return null;
    }
}
