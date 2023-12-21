package org.ulpgc.dacd.model;

import java.time.Instant;
import java.util.List;

public record Hotel(String name, String location, String ss, Instant ts, List<Rate> rates) {
}