package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherController {
    private final OpenWeatherMapSupplier openWeatherMapSupplier;
    private final JmsWeatherStore jmsWeatherStore;
    private final String brokerURL;
    private final String topicName;
    private final long refreshFrequency;

    public WeatherController(OpenWeatherMapSupplier openWeatherMapSupplier, JmsWeatherStore jmsWeatherStore, String brokerURL, String topicName, long refreshFrequency) {
        this.openWeatherMapSupplier = openWeatherMapSupplier;
        this.jmsWeatherStore = jmsWeatherStore;
        this.brokerURL = brokerURL;
        this.topicName = topicName;
        this.refreshFrequency = refreshFrequency;
    }

    public void execute(Location... locations) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for (Location location : locations) {
                    List<Weather> weatherList = openWeatherMapSupplier.getWeather(location, Instant.now());
                    for (Weather weather : weatherList) {
                        jmsWeatherStore.sendWeatherToBroker(weather, brokerURL, topicName);
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, refreshFrequency);
    }

}
