package org.ulpgc.dacd.control;

public interface EventStore {
    void createTables();
    void insertHotel(String json);
    void insertCheckInOut(String json);
    void insertHotelRates(String json);
    void insertWeather(String weatherEvent);
}
