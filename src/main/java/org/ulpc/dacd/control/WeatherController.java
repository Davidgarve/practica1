package org.ulpc.dacd.control;

import org.ulpc.dacd.model.Location;
import org.ulpc.dacd.model.Weather;


import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherController {
    private final OpenWeatherMapSupplier openWeatherMapSupplier;
    private final SqliteWeatherStore sqliteWeatherStore;

    public WeatherController(OpenWeatherMapSupplier openWeatherMapSupplier, SqliteWeatherStore sqliteWeatherStore) {
        this.openWeatherMapSupplier = openWeatherMapSupplier;
        this.sqliteWeatherStore = sqliteWeatherStore;
    }

    public void execute(Location... locations) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateAndSave(locations);
            }
        };

        timer.scheduleAtFixedRate(task, 0, 6 * 60 * 60 * 1000);
    }

    public void updateAndSave(Location... locations) {
        for (Location location : locations) {
            String tableName = location.getName().replace(" ", "_");
            List<Weather> weatherData = openWeatherMapSupplier.getWeather(location, Instant.now());
            sqliteWeatherStore.createTable(tableName);
            sqliteWeatherStore.insertWeather(tableName, weatherData);
            System.out.println("Data updated and saved in the table.");
        }

        System.out.println("Data updated and saved in the database.");
    }
}
