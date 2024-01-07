package org.ulpgc.dacd.control;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class HotelBusinessLogic {
    private final SQLiteEventStore eventStore;

    public HotelBusinessLogic(SQLiteEventStore eventStore) {
        this.eventStore = eventStore;
    }

    public List<String> getAvailableHotelLocations() {
        return eventStore.getAvailableHotelLocations();
    }

    public List<Map<String, Object>> getHotelInfoForDates(String location, LocalDate checkInDate, LocalDate checkOutDate) {
        return eventStore.getHotelInfoForDates(location, checkInDate, checkOutDate);
    }
}
