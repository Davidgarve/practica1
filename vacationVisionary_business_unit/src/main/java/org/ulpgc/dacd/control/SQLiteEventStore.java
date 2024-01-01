package org.ulpgc.dacd.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteEventStore {
    private static final String JDBC_URL = "jdbc:sqlite:datamark.db";

    static Connection getConnection() {
        try {
            return DriverManager.getConnection(JDBC_URL);
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener la conexión a la base de datos", e);
        }
    }

    public static void createTables() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Hotel (" +
                    "hotel_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "hotel_name TEXT UNIQUE," +
                    "location TEXT," +
                    "ts TEXT," +
                    "ss TEXT" +
                    ");");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS CheckInOut (" +
                    "checkinout_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "hotel_id INTEGER," +
                    "check_in TEXT," +
                    "check_out TEXT," +
                    "UNIQUE(hotel_id, check_in, check_out)," +  // Restricción única en la combinación de columnas
                    "FOREIGN KEY (hotel_id) REFERENCES Hotel(hotel_id)" +
                    ");");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS HotelRates (" +
                    "hotelrates_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "checkinout_id INTEGER," +
                    "name TEXT," +
                    "rate REAL," +
                    "tax REAL," +
                    "UNIQUE(checkinout_id, name, rate, tax)," +  // Restricción única en la combinación de columnas
                    "FOREIGN KEY (checkinout_id) REFERENCES CheckInOut(checkinout_id)" +
                    ");");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Weather (" +
                    "weather_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "pop REAL," +
                    "wind_speed REAL," +
                    "temp REAL," +
                    "humidity INTEGER," +
                    "clouds INTEGER," +
                    "prediction_date TEXT," +
                    "ts TEXT," +
                    "ss TEXT," +
                    "location_name TEXT," +
                    "UNIQUE(prediction_date, location_name)" +
                    ");");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertHotel(String json) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO Hotel (hotel_name, location, ts, ss) VALUES (?, ?, ?, ?) ON CONFLICT (hotel_name) DO UPDATE SET location=excluded.location, ts=excluded.ts, ss=excluded.ss")) {

            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

            statement.setString(1, jsonObject.get("hotelName").getAsString());
            statement.setString(2, jsonObject.get("location").getAsString());
            statement.setString(3, jsonObject.get("ts").getAsString());
            statement.setString(4, jsonObject.get("ss").getAsString());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void insertCheckInOut(String json) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO CheckInOut (hotel_id, check_in, check_out) VALUES (?, ?, ?) ON CONFLICT(hotel_id, check_in, check_out) DO NOTHING")) {

            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

            int hotelId = getHotelId(jsonObject.get("hotelName").getAsString());
            statement.setInt(1, hotelId);
            statement.setString(2, jsonObject.get("checkIn").getAsString());
            statement.setString(3, jsonObject.get("checkOut").getAsString());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertHotelRates(String json) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO HotelRates (checkinout_id, name, rate, tax) VALUES (?, ?, ?, ?) ON CONFLICT(checkinout_id, name, rate, tax) DO NOTHING")) {

            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            int checkInOutId = getCheckInOutId(jsonObject);

            JsonArray ratesArray = jsonObject.getAsJsonArray("rates");

            for (JsonElement rateElement : ratesArray) {
                JsonObject rateJson = rateElement.getAsJsonObject();

                String name = rateJson.get("name").getAsString();
                Double rate = rateJson.get("rate").getAsDouble();
                Double tax = rateJson.get("tax").getAsDouble();

                statement.setInt(1, checkInOutId);
                statement.setString(2, name);
                statement.setDouble(3, rate);
                statement.setDouble(4, tax);

                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void insertWeather(String weatherEvent) {
        try {
            Connection connection = getConnection();

            JsonObject jsonObject = JsonParser.parseString(weatherEvent).getAsJsonObject();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT OR REPLACE INTO Weather (pop, wind_speed, temp, humidity, clouds, prediction_date, ss, ts, location_name) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );

            preparedStatement.setDouble(1, jsonObject.get("pop").getAsDouble());
            preparedStatement.setDouble(2, jsonObject.get("windSpeed").getAsDouble());
            preparedStatement.setDouble(3, jsonObject.get("temp").getAsDouble());
            preparedStatement.setInt(4, jsonObject.get("humidity").getAsInt());
            preparedStatement.setInt(5, jsonObject.get("clouds").getAsInt());
            preparedStatement.setString(6, jsonObject.get("predictionDate").getAsString());
            preparedStatement.setString(7, jsonObject.get("ss").getAsString());
            preparedStatement.setString(8, jsonObject.get("ts").getAsString());

            JsonObject locationJson = jsonObject.getAsJsonObject("location");
            String locationName = locationJson.get("name").getAsString();

            preparedStatement.setString(9, locationName);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private static int getHotelId(String hotelName) {
        int hotelId = -1;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT hotel_id FROM Hotel WHERE hotel_name = ?")) {

            statement.setString(1, hotelName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    hotelId = resultSet.getInt("hotel_id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hotelId;
    }

    private static int getCheckInOutId(JsonObject jsonObject) {
        int checkInOutId = -1;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT checkinout_id FROM CheckInOut WHERE hotel_id = ? AND check_in = ? AND check_out = ?")) {

            int hotelId = getHotelId(jsonObject.get("hotelName").getAsString());
            statement.setInt(1, hotelId);
            statement.setString(2, jsonObject.get("checkIn").getAsString());
            statement.setString(3, jsonObject.get("checkOut").getAsString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    checkInOutId = resultSet.getInt("checkinout_id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return checkInOutId;
    }


    public Map<String, Object> getWeatherInfo(String locationName, String predictionDate) {
        Map<String, Object> weatherMap = new HashMap<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM Weather WHERE location_name = ? AND strftime('%Y-%m-%d', prediction_date) = ?")) {

            statement.setString(1, locationName);
            statement.setString(2, predictionDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    weatherMap.put("pop", resultSet.getDouble("pop"));
                    weatherMap.put("windSpeed", resultSet.getDouble("wind_speed"));
                    weatherMap.put("temp", resultSet.getDouble("temp"));
                    weatherMap.put("humidity", resultSet.getInt("humidity"));
                    weatherMap.put("clouds", resultSet.getInt("clouds"));
                    weatherMap.put("predictionDate", resultSet.getString("prediction_date"));
                    weatherMap.put("ss", resultSet.getString("ss"));
                    weatherMap.put("ts", resultSet.getString("ts"));
                    weatherMap.put("location_name", resultSet.getString("location_name"));

                    return weatherMap;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<String> getAvailableDates() {
        List<String> availableDates = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT prediction_date FROM Weather");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String date = resultSet.getString("prediction_date");
                date = date.split(" ")[0];

                availableDates.add(date);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableDates;
    }

    public static List<String> getAvailableDatesInRange(LocalDate startDate, LocalDate endDate) {
        List<String> availableDates = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT DISTINCT strftime('%Y-%m-%d', prediction_date) AS date " +
                             "FROM Weather " +
                             "WHERE strftime('%Y-%m-%d', prediction_date) BETWEEN ? AND ?")) {

            statement.setString(1, startDate.toString());
            statement.setString(2, endDate.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    availableDates.add(resultSet.getString("date"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableDates;
    }

    public List<String> getAvailableHotelLocations() {
        List<String> availableLocations = new ArrayList<>();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT DISTINCT location FROM Hotel";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                availableLocations.add(resultSet.getString("location"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableLocations;
    }

    public List<Map<String, Object>> getHotelInfoForDates(String location, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Map<String, Object>> hotelInfoList = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT Hotel.hotel_name, Hotel.location, Hotel.ts, Hotel.ss, CheckInOut.check_in, CheckInOut.check_out, CheckInOut.checkinout_id, HotelRates.name, HotelRates.rate, HotelRates.tax " +
                             "FROM Hotel " +
                             "JOIN CheckInOut ON Hotel.hotel_id = CheckInOut.hotel_id " +
                             "JOIN HotelRates ON CheckInOut.checkinout_id = HotelRates.checkinout_id " +
                             "WHERE Hotel.location = ? " +
                             "AND CheckInOut.check_in = ? " +
                             "AND CheckInOut.check_out = ?")) {

            statement.setString(1, location);
            statement.setString(2, checkInDate.toString());
            statement.setString(3, checkOutDate.toString());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Map<String, Object> hotelInfo = new HashMap<>();
                hotelInfo.put("hotelName", resultSet.getString("hotel_name"));
                hotelInfo.put("location", resultSet.getString("location"));
                hotelInfo.put("ts", resultSet.getString("ts"));
                hotelInfo.put("ss", resultSet.getString("ss"));
                hotelInfo.put("checkIn", resultSet.getString("check_in"));
                hotelInfo.put("checkOut", resultSet.getString("check_out"));
                hotelInfo.put("checkInOutId", resultSet.getInt("checkinout_id"));

                List<Map<String, Object>> ratesList = new ArrayList<>();
                do {
                    Map<String, Object> rateInfo = new HashMap<>();
                    rateInfo.put("name", resultSet.getString("name"));
                    rateInfo.put("rate", resultSet.getDouble("rate"));
                    rateInfo.put("tax", resultSet.getDouble("tax"));
                    ratesList.add(rateInfo);
                } while (resultSet.next() && resultSet.getString("hotel_name").equals(hotelInfo.get("hotelName")));

                hotelInfo.put("rates", ratesList);
                hotelInfoList.add(hotelInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hotelInfoList;
    }
}
