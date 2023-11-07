package org.ulpc.dacd.model;
import java.time.Instant;

public class Weather {
    private Instant ts;
    private double feels_like;
    private double speed;
    private double temp;
    private int humidity;
    private Location location;

    public double getFeelsLike() {
        return feels_like;
    }

    public void setFeelsLike(double feels_like) {
        this.feels_like = feels_like;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "feels_like=" + feels_like +
                ", speed=" + speed +
                ", temp=" + temp +
                ", humidity=" + humidity +
                '}';
    }
}
