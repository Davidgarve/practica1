package org.ulpgc.dacd.model;


import java.time.Instant;
import java.util.List;

public record Hotel(String name, String location, String ss, Instant ts, String website, double rate, double tax, List<String> averagePriceDays, List<String> cheapPriceDays, List<String> highPriceDays) {
}
