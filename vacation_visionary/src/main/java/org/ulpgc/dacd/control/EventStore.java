package org.ulpgc.dacd.control;

public interface EventStore {
    void createTables();
    void saveHotel(String json);
    void saveWeather(String json);
}
