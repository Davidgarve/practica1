package org.ulpc.dacd.model;

import java.time.Instant;

public record Weather(double pop, double windSpeed, double temp, int humidity, String predictionDate, String ss, int clouds, Instant ts, Location location) {

    @Override
    public String toString() {
        return "Weather{" +
                "pop=" + pop() +
                ", windSpeed=" + windSpeed() +
                ", temperature=" + temp() +
                ", humidity=" + humidity() +
                ", predictionDate='" + predictionDate() + '\'' +
                ", clouds=" + clouds() +
                ", ts=" + ts() +
                ", ss=" + ss() +
                ", location=" + location().toString() +
                '}';
    }
}