package org.ulpgc.dacd.model;

import java.time.Instant;

public record Weather(double pop, double windSpeed, double temp, int humidity, String predictionDate, String ss, int clouds, Instant ts, Location location) {

    @Override
    public String toString() {
        return "Weather{" +
                "pop=" + pop +
                ", windSpeed=" + windSpeed +
                ", temp=" + temp +
                ", humidity=" + humidity +
                ", predictionDate='" + predictionDate + '\'' +
                ", ss='" + ss + '\'' +
                ", clouds=" + clouds +
                ", ts=" + ts +
                ", location=" + location +
                '}';
    }
}
