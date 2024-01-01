package org.ulpgc.dacd.control;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TripPlannerHotel {
    private final SQLiteEventStore eventStore;

    public TripPlannerHotel(SQLiteEventStore eventStore) {
        this.eventStore = eventStore;
    }

    public List<String> getAvailableHotelLocations() {
        return eventStore.getAvailableHotelLocations();
    }

    public List<Map<String, Object>> getHotelInfoForDates(String location, LocalDate checkInDate, LocalDate checkOutDate) {
        return eventStore.getHotelInfoForDates(location, checkInDate, checkOutDate);
    }

    public static String buildHotelInfoHTML(List<Map<String, Object>> hotelData) {
        StringBuilder htmlContent = new StringBuilder("<html><body>");

        if (hotelData.isEmpty()) {
            htmlContent.append("<h2>No hay datos disponibles para la ubicación y fechas seleccionadas.</h2>");
        } else {
            for (Map<String, Object> hotelInfo : hotelData) {
                String hotelName = hotelInfo.get("hotelName").toString();
                String location = hotelInfo.get("location").toString();
                String checkIn = hotelInfo.get("checkIn").toString();
                String checkOut = hotelInfo.get("checkOut").toString();

                htmlContent.append("<h2>").append(hotelName).append("</h2>");
                htmlContent.append("<p><b>Ubicación:</b> ").append(location).append("</p>");
                htmlContent.append("<p><b>Check-In:</b> ").append(checkIn).append("</p>");
                htmlContent.append("<p><b>Check-Out:</b> ").append(checkOut).append("</p>");

                List<Map<String, Object>> ratesList = (List<Map<String, Object>>) hotelInfo.get("rates");
                if (ratesList != null && !ratesList.isEmpty()) {
                    htmlContent.append("<h3>Rates:</h3>");
                    htmlContent.append("<ul>");
                    for (Map<String, Object> rateInfo : ratesList) {
                        String rateName = rateInfo.get("name").toString();
                        Double rateValue = (Double) rateInfo.get("rate");
                        Double taxValue = (Double) rateInfo.get("tax");

                        htmlContent.append("<li>").append(rateName).append(": ").append(rateValue + taxValue).append("</li>");
                    }
                    htmlContent.append("</ul>");
                } else {
                    htmlContent.append("<p>No hay datos de tarifas disponibles para este hotel en las fechas seleccionadas.</p>");
                }
            }
        }

        htmlContent.append("</body></html>");
        return htmlContent.toString();
    }
}
