package org.ulpgc.dacd.control;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class HotelBusinessLogic implements HotelBusinessLogicInterface{
    private final SQLiteEventRepository eventStore;

    public HotelBusinessLogic(SQLiteEventRepository eventStore) {
        this.eventStore = eventStore;
    }

    public List<String> getAvailableHotelLocations() {
        return eventStore.getAvailableHotelLocations();
    }

    public List<Map<String, Object>> getHotelInfoForDates(String location, LocalDate checkInDate, LocalDate checkOutDate) {
        return eventStore.getHotelInfoForDates(location, checkInDate, checkOutDate);
    }
}
