package org.ulpgc.dacd.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.model.Hotel;
import org.ulpgc.dacd.model.Rate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class XoteloSupplier implements HotelSupplier{
    private final String hotelKey;

    public XoteloSupplier(String apiKey) {
        this.hotelKey = apiKey;
    }

    public List<Hotel> getHotel(String hotelName, String checkInDate, String checkOutDate, String location, Instant ts) {
        try {
            String ratesApiUrl = "https://data.xotelo.com/api/rates" +
                    "?hotel_key=" + hotelKey +
                    "&chk_in=" + checkInDate +
                    "&chk_out=" + checkOutDate;

            String ratesApiResponse = performApiCall(ratesApiUrl);

            List<Hotel> hotel = parseXoteloApiResponse(ratesApiResponse, location, ts, hotelName);

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

    private List<Hotel> parseXoteloApiResponse(String ratesApiResponse, String location, Instant ts, String hotelName) {
        List<Hotel> hotelRates = new ArrayList<>();
        JsonObject ratesJsonObject = JsonParser.parseString(ratesApiResponse).getAsJsonObject();

        if (ratesJsonObject.has("result") && ratesJsonObject.getAsJsonObject("result").has("rates")) {
            JsonArray ratesArray = ratesJsonObject.getAsJsonObject("result").getAsJsonArray("rates");

            List<Rate> hotelRatesList = new ArrayList<>();

            for (int i = 0; i < ratesArray.size(); i++) {
                JsonObject rateData = ratesArray.get(i).getAsJsonObject();

                String providerName = rateData.get("name").getAsString();
                double rate = rateData.get("rate").getAsDouble();
                double tax = rateData.get("tax").getAsDouble();

                Rate rateInfo = new Rate(providerName, rate, tax);
                hotelRatesList.add(rateInfo);
            }

            Hotel hotel = new Hotel(hotelName, location, "Xotelo", ts, hotelRatesList);
            System.out.println(hotel);
            hotelRates.add(hotel);
        }

        return hotelRates;
    }
}