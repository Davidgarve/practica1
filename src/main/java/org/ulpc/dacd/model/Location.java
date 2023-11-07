package org.ulpc.dacd.model;

public class Location {
    private double lat;
    private double lon;
    private String island;

    public Location(double lat, double lon, String name) {
        this.lat = lat;
        this.lon = lon;
        this.island = name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getIsland() {
        return island;
    }
}
