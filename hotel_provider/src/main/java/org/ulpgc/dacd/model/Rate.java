package org.ulpgc.dacd.model;

public record Rate(String name, double rate, double tax) {
    @Override
    public String toString() {
        return "Rate{" +
                "name='" + name + '\'' +
                ", rate=" + rate +
                ", tax=" + tax +
                '}';
    }
}
