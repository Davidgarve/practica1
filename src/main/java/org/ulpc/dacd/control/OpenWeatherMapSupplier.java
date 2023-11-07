package org.ulpc.dacd.control;
import org.ulpc.dacd.model.Location;
import org.ulpc.dacd.model.Weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.List;

public class OpenWeatherMapSupplier {

    public Weather getWeather(Location location, Instant ts) {
        try {
            String apiUrl  = "https://api.openweathermap.org/data/2.5/weather?lat=" + location.getLat() + "&lon=" + location.getLon() + "&appid=d41a06840247e2449a1ddc74dd6789da";
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
                System.out.println(responseData);
                Weather weather = parseWeatherData(responseData);

                return weather;
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
        // Parse responseData and create a Weather object
        // Example: You can use JSON parsing libraries like Jackson or Gson
        // to parse the JSON response and create a Weather object.
        // Return the Weather object.
        return new Weather(); // Replace this with the actual parsing logic.
    }

}
