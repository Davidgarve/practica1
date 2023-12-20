package org.ulpgc.dacd.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.model.Hotel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class XoteloSupplier {
    private final String apiKey;

    public XoteloSupplier(String apiKey) {
        this.apiKey = apiKey;
    }

    public List<Hotel> getHotel(String hotelName, String checkInDate, String checkOutDate, String location, Instant ts) {
        try {
            // Perform the rates API call
            String ratesApiUrl = "https://data.xotelo.com/api/rates" +
                    "?hotel_key=" + apiKey +
                    "&chk_in=" + checkInDate +
                    "&chk_out=" + checkOutDate;

            String ratesApiResponse = performApiCall(ratesApiUrl);

            // Perform the heatmap API call
            String heatmapApiUrl = "https://data.xotelo.com/api/heatmap" +
                    "?hotel_key=" + apiKey +
                    "&chk_out=" + checkOutDate;

            String heatmapApiResponse = performApiCall(heatmapApiUrl);

            // Parse both responses
            List<Hotel> hotel = parseXoteloApiResponse(ratesApiResponse, heatmapApiResponse, location, ts, hotelName);

            return hotel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String performApiCall(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();
        } else {
            System.err.println("Error " + responseCode);
        }

        connection.disconnect();

        return null;
    }

    private List<Hotel> parseXoteloApiResponse(String ratesApiResponse, String heatmapApiResponse, String location, Instant ts, String hotelName) {
        List<Hotel> hotelRates = new ArrayList<>();
        JsonObject ratesJsonObject = JsonParser.parseString(ratesApiResponse).getAsJsonObject();
        JsonObject heatmapJsonObject = JsonParser.parseString(heatmapApiResponse).getAsJsonObject();

        if (ratesJsonObject.has("result") && ratesJsonObject.getAsJsonObject("result").has("rates")) {
            JsonArray ratesArray = ratesJsonObject.getAsJsonObject("result").getAsJsonArray("rates");

            for (int i = 0; i < ratesArray.size(); i++) {
                JsonObject rateData = ratesArray.get(i).getAsJsonObject();

                String providerName = rateData.get("name").getAsString();
                double rate = rateData.get("rate").getAsDouble();
                double tax = rateData.has("tax") ? rateData.get("tax").getAsDouble() : 0.0;

                List<String> averagePriceDays = extractDaysList(heatmapJsonObject, "average_price_days");
                List<String> cheapPriceDays = extractDaysList(heatmapJsonObject, "cheap_price_days");
                List<String> highPriceDays = extractDaysList(heatmapJsonObject, "high_price_days");

                Hotel hotel = createHotelObject(hotelName, location, ts, providerName, rate, tax, averagePriceDays, cheapPriceDays, highPriceDays);
                System.out.println(hotel);
                hotelRates.add(hotel);
            }
        }

        return hotelRates;
    }

    private List<String> extractDaysList(JsonObject heatmapJsonObject, String key) {
        List<String> daysList = new ArrayList<>();

        if (heatmapJsonObject.has("result") && heatmapJsonObject.getAsJsonObject("result").has("heatmap")) {
            JsonObject heatmapData = heatmapJsonObject.getAsJsonObject("result").getAsJsonObject("heatmap");

            if (heatmapData.has(key)) {
                JsonArray daysArray = heatmapData.getAsJsonArray(key);

                for (int i = 0; i < daysArray.size(); i++) {
                    daysList.add(daysArray.get(i).getAsString());
                }
            }
        }

        return daysList;
    }

    private Hotel createHotelObject(String hotelName, String location, Instant ts, String providerName, double rate, double tax, List<String> averagePriceDays, List<String> cheapPriceDays, List<String> highPriceDays) {
        return new Hotel(hotelName, location, "Xotelo", ts, providerName, rate, tax, averagePriceDays, cheapPriceDays, highPriceDays);
    }
}
