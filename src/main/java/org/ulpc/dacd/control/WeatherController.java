package org.ulpc.dacd.control;

import org.ulpc.dacd.model.Location;
import org.ulpc.dacd.model.Weather;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherController {
    private OpenWeatherMapSupplier openWeatherMapSupplier;
    private  SqliteWeatherStore sqliteWeatherStore;

    public WeatherController(OpenWeatherMapSupplier openWeatherMapSupplier, SqliteWeatherStore sqliteWeatherStore) {
        this.openWeatherMapSupplier = openWeatherMapSupplier;
        this.sqliteWeatherStore = sqliteWeatherStore;
    }

    public void execute(Location... locations) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> updateWeatherData(locations), 0, 6, TimeUnit.HOURS);
    }

    private void updateWeatherData(Location... locations) {
        for (Location location : locations) {
            createTablesAndInsertData(location);
        }

        System.out.println("Datos actualizados en la base de datos.");
    }

    private void createTablesAndInsertData(Location location) {
        String tableName = location.getName().replace(" ", "_"); // Usa el nombre de la ubicación como nombre de la tabla
        List<Weather> weatherData = openWeatherMapSupplier.getWeather(location, Instant.now());
        sqliteWeatherStore.createTable(tableName);
        sqliteWeatherStore.insertWeather(tableName, weatherData);
    }
}
