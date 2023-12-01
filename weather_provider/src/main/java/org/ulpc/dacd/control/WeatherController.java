package org.ulpc.dacd.control;

import org.ulpc.dacd.model.Location;
import org.ulpc.dacd.model.Weather;

import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherController {
    private final OpenWeatherMapSupplier openWeatherMapSupplier;
    private final JmsWeatherStore jmsWeatherStore;

    public WeatherController(OpenWeatherMapSupplier openWeatherMapSupplier, JmsWeatherStore jmsWeatherStore) {
        this.openWeatherMapSupplier = openWeatherMapSupplier;
        this.jmsWeatherStore = jmsWeatherStore;
    }

    public void execute(Location... locations) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for (Location location : locations) {
                    // Obtener datos meteorológicos
                    List<Weather> weatherList = openWeatherMapSupplier.getWeather(location, Instant.now());

                    // Enviar el clima al broker directamente para cada objeto Weather en la lista
                    for (Weather weather : weatherList) {
                        jmsWeatherStore.sendWeatherToBroker(weather, "tcp://localhost:61616", "prediction.weather");
                    }
                }
            }
        };

        // Programar la ejecución cada seis horas (6 * 60 * 60 * 1000)
        timer.scheduleAtFixedRate(task, 0, 6 * 60 * 60 * 1000);
    }

}
