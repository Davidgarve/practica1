package org.ulpgc.dacd.model;

import java.time.Instant;
import java.util.List;

public class Hotel {
    private String name;
    private String location;
    private Instant ts;
    private String ss;
    private String checkIn;
    private String checkOut;
    private List<Rate> rates;

    public Hotel(String name, String location, Instant ts, String ss, String checkIn, String checkOut, List<Rate> rates) {
        this.name = name;
        this.location = location;
        this.ts = ts;
        this.ss = ss;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.rates = rates;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", ts=" + ts +
                ", ss='" + ss + '\'' +
                ", checkIn='" + checkIn + '\'' +
                ", checkOut='" + checkOut + '\'' +
                ", rates=" + rates +
                '}';
    }
}

