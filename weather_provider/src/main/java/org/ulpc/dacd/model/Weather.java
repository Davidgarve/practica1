package org.ulpc.dacd.model;

import java.time.Instant;

public class Weather {
    private double pop;
    private double windSpeed;
    private double temp;
    private int humidity;
    private String predictionDate;
    private int clouds;
    private Instant ts;
    private Location location;


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getPop() {
        return pop;
    }

    public void setPop(double pop) {
        this.pop = pop;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
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

    public String getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(String predictionDate) {
        this.predictionDate = predictionDate;
    }

    public int getClouds() {
        return clouds;
    }

    public void setClouds(int clouds) {
        this.clouds = clouds;
    }

    public void setTs(Instant ts) {
        this.ts = ts;
    }
    public Instant getTs() {
        return ts;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "pop=" + getPop() +
                ", windSpeed=" + getWindSpeed() +
                ", temperature=" + getTemp() +
                ", humidity=" + getHumidity() +
                ", predictionDate='" + getPredictionDate() + '\'' +
                ", clouds=" + getClouds() +
                ", ts=" + getTs() +
                ", location=" + getLocation().toString() +
                '}';
    }
}
