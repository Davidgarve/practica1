package org.ulpgc.dacd.control;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EventStore {
    void createTables();
    void saveHotel(String json);
    void saveWeather(String json);
}
