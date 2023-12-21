package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;

import java.sql.*;

public class SQLiteEventStore {

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:datamark.db";
        return DriverManager.getConnection(url);
    }

    public static void createWeatherTable() {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            // Crea la tabla para eventos meteorológicos
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS weather_events (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "pop REAL," +
                            "windSpeed REAL," +
                            "temperature REAL," +
                            "humidity INTEGER," +
                            "predictionDate TEXT," +
                            "clouds INTEGER," +
                            "ts TEXT," +
                            "ss TEXT," +
                            "location_name TEXT" +
                            ")"
            );

            // Cierra la conexión y el Statement
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void insertWeatherEvent(JsonObject weatherEvent) {
        try {
            Connection connection = getConnection();

            // Preparar la declaración SQL para insertar un evento meteorológico
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO weather_events (pop, windSpeed, temperature, humidity, predictionDate, ss, clouds, ts, location_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );

            // Establecer los parámetros en la declaración
            preparedStatement.setDouble(1, weatherEvent.get("pop").getAsDouble());
            preparedStatement.setDouble(2, weatherEvent.get("windSpeed").getAsDouble());
            preparedStatement.setDouble(3, weatherEvent.get("temp").getAsDouble());
            preparedStatement.setInt(4, weatherEvent.get("humidity").getAsInt());
            preparedStatement.setString(5, weatherEvent.get("predictionDate").getAsString());
            preparedStatement.setString(6, weatherEvent.get("ss").getAsString());
            preparedStatement.setInt(7, weatherEvent.get("clouds").getAsInt());
            preparedStatement.setString(8, weatherEvent.get("ts").getAsString());

            // Obtener el objeto Location del JSON
            JsonObject locationJson = weatherEvent.getAsJsonObject("location");
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


}