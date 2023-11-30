package org.ulpc.dacd.control;

import org.ulpc.dacd.model.Location;

import java.util.Timer;
import java.util.TimerTask;

public class WeatherController {
    private final OpenWeatherMapSupplier openWeatherMapSupplier;

    public WeatherController(OpenWeatherMapSupplier openWeatherMapSupplier) {
        this.openWeatherMapSupplier = openWeatherMapSupplier;
    }

    public void execute(Location... locations) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //updateAndSave(locations);
            }
        };

        timer.scheduleAtFixedRate(task, 0, 6 * 60 * 60 * 1000);
    }

}
