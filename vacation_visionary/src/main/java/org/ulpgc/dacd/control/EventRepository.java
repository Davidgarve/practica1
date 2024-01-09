package org.ulpgc.dacd.control;

public interface EventRepository {
    void createTables();
    void saveHotel(String json);
    void saveWeather(String json);
}
