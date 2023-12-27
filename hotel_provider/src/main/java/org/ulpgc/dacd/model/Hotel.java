package org.ulpgc.dacd.model;

import java.time.Instant;
import java.util.List;

public record Hotel(String hotelName, String location, Instant ts, String ss, String checkIn, String checkOut, List<Rate> rates) {
    @Override
    public String toString() {
        return "Hotel{" +
                "hotelName='" + hotelName + '\'' +
                ", location='" + location + '\'' +
                ", ts=" + ts +
                ", ss='" + ss + '\'' +
                ", checkIn='" + checkIn + '\'' +
                ", checkOut='" + checkOut + '\'' +
                ", rates=" + rates +
                '}';
    }
}
