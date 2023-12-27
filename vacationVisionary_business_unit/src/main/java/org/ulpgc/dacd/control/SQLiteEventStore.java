package org.ulpgc.dacd.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.*;

public class SQLiteEventStore {
    private static final String JDBC_URL = "jdbc:sqlite:datamark.db";

    private static Connection getConnection() {
        try {
            return DriverManager.getConnection(JDBC_URL);
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener la conexión a la base de datos", e);
        }
    }

    public static void createTables() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Crear la tabla Hotel
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Hotel (" +
                    "hotel_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "hotel_name TEXT UNIQUE," +
                    "location TEXT," +
                    "ts TEXT," +
                    "ss TEXT" +
                    ");");

            // Crear la tabla CheckInOut
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS CheckInOut (" +
                    "checkinout_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "hotel_id INTEGER," +
                    "check_in TEXT," +
                    "check_out TEXT," +
                    "UNIQUE(hotel_id, check_in, check_out)," +  // Restricción única en la combinación de columnas
                    "FOREIGN KEY (hotel_id) REFERENCES Hotel(hotel_id)" +
                    ");");

            // Crear la tabla HotelRates
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS HotelRates (" +
                    "hotelrates_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "checkinout_id INTEGER," +
                    "name TEXT," +
                    "rate REAL," +
                    "tax REAL," +
                    "UNIQUE(checkinout_id, name, rate, tax)," +  // Restricción única en la combinación de columnas
                    "FOREIGN KEY (checkinout_id) REFERENCES CheckInOut(checkinout_id)" +
                    ");");

            // Crear la tabla Weather
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

                statement.executeUpdate(); // Ejecutar la operación de inserción para cada tarifa
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }









    public void insertWeather(String weatherEvent) {
        try {
            Connection connection = getConnection();

            // Convertir la cadena JSON a un objeto JsonObject
            JsonObject jsonObject = JsonParser.parseString(weatherEvent).getAsJsonObject();

            // Preparar la declaración SQL para insertar o actualizar un evento meteorológico
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT OR REPLACE INTO Weather (pop, wind_speed, temp, humidity, clouds, prediction_date, ss, ts, location_name) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );

            // Establecer los parámetros en la declaración
            preparedStatement.setDouble(1, jsonObject.get("pop").getAsDouble());
            preparedStatement.setDouble(2, jsonObject.get("windSpeed").getAsDouble());
            preparedStatement.setDouble(3, jsonObject.get("temp").getAsDouble());
            preparedStatement.setInt(4, jsonObject.get("humidity").getAsInt());
            preparedStatement.setInt(5, jsonObject.get("clouds").getAsInt());
            preparedStatement.setString(6, jsonObject.get("predictionDate").getAsString());
            preparedStatement.setString(7, jsonObject.get("ss").getAsString());
            preparedStatement.setString(8, jsonObject.get("ts").getAsString());

            // Obtener el objeto Location del JSON
            JsonObject locationJson = jsonObject.getAsJsonObject("location");
            // Obtener la variable 'name' de Location
            String locationName = locationJson.get("name").getAsString();

            // Establecer el nombre de la ubicación en la declaración
            preparedStatement.setString(9, locationName);

            // Ejecutar la declaración
            preparedStatement.executeUpdate();

            // Cerrar la conexión y el PreparedStatement
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // Métodos adicionales para obtener IDs necesarios
    private static int getHotelId(String hotelName) {
        int hotelId = -1; // Valor predeterminado si no se encuentra el hotel

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
        int checkInOutId = -1; // Valor predeterminado si no se encuentra el checkinout

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

}