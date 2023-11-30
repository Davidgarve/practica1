package org.ulpc.dacd.control;

import org.ulpc.dacd.model.Location;
import org.ulpc.dacd.model.Weather;

import java.time.Instant;

public interface WeatherSupplier {
     Weather getWeather(Location location, Instant ts);
}
