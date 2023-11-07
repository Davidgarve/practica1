package org.ulpc.dacd.model;
import java.time.Instant;

public class Weather {
    private Instant ts;
    private double rain;
    private double windSpeed;
    private double temp;
    private int humidity;
    private Location location;

    public Instant getTs() {
        return ts;
    }

    public double getRain() {
        return rain;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getTemp() {
        return temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public Location getLocation() {
        return location;
    }
}
