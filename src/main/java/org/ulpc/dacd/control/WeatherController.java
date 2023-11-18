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
                save(locations);
            }
        };

        timer.scheduleAtFixedRate(task, 0, 6 * 60 * 60 * 1000);
    }

    public void save(Location... locations) {
        for (Location location : locations) {
            updateWeatherData(location);
        }

        System.out.println("Data updated and saved in the database.");
    }

    private void updateWeatherData(Location... locations) {
        for (Location location : locations) {
            createTablesAndInsertData(location);
        }

        System.out.println("Updated table in the database.");
    }

    private void createTablesAndInsertData(Location location) {
        String tableName = location.getName().replace(" ", "_");
        List<Weather> weatherData = openWeatherMapSupplier.getWeather(location, Instant.now());
        sqliteWeatherStore.createTable(tableName);
        sqliteWeatherStore.insertWeather(tableName, weatherData);
    }
}
