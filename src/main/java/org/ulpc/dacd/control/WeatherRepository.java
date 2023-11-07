package org.ulpc.dacd.control;

import org.ulpc.dacd.model.Location;
import org.ulpc.dacd.model.Weather;

import java.time.Instant;

public interface WeatherRepository {
    //save(weather)
    public Weather getWeather(Location location, Instant ts);

}
