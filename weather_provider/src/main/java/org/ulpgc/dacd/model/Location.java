package org.ulpgc.dacd.model;

public class Location {
    private final double lat;
    private final double lon;
    private final String name;

    public Location(double lat, double lon, String name) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }


    @Override
    public String toString() {
        return "Location{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", name='" + name + '\'' +
                '}';
    }
}
