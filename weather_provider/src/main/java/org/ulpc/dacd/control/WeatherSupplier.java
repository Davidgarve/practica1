package org.ulpc.dacd.control;

import org.ulpc.dacd.model.Location;
import org.ulpc.dacd.model.Weather;

import java.time.Instant;
import java.util.List;

public interface WeatherSupplier {
     List<Weather> getWeather(Location location, Instant ts);
}
