package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Main <api-key>");
            System.exit(1);
        }

        String apiKey = args[0];
        String locationsFile = "locations.txt";
        List<Location> locations = readLocationsFromFile(locationsFile);

        String brokerURL = "tcp://localhost:61616";
        String topicName = "prediction.weather";

        OpenWeatherMapSupplier supplier = new OpenWeatherMapSupplier(apiKey);
        JmsWeatherStore jmsWeatherStore = new JmsWeatherStore();
        WeatherController weatherController = new WeatherController(supplier, jmsWeatherStore, brokerURL, topicName);
        weatherController.execute(locations.toArray(new Location[0]));
    }

    private static List<Location> readLocationsFromFile(String fileName) {
        List<Location> locations = new ArrayList<>();

        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                double latitude = Double.parseDouble(parts[0]);
                double longitude = Double.parseDouble(parts[1]);
                String name = parts[2];
                Location location = new Location(latitude, longitude, name);
                locations.add(location);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return locations;
    }
}
