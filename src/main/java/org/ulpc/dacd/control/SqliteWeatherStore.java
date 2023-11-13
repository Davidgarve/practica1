package org.ulpc.dacd.control;

import org.ulpc.dacd.model.Weather;

import java.sql.*;
import java.util.List;

public class SqliteWeatherStore {

    public void createTable(String tableName) {
        try (Connection connection = connect("weather.db")) {
            Statement statement = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "dt_txt TEXT UNIQUE," +
                    "temperature REAL DEFAULT 0," +
                    "wind_speed REAL DEFAULT 0," +
                    "humidity INTEGER DEFAULT 0," +
                    "pop REAL DEFAULT 0," +
                    "clouds_all INTEGER DEFAULT 0" +
                    ");";
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void insertWeather(String tableName, List<Weather> weatherList) {
        try (Connection connection = connect("weather.db")) {
            String insertSQL = "INSERT INTO " + tableName +
                    " (dt_txt, temperature, wind_speed, humidity, pop, clouds_all) " +
                    "VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT(dt_txt) DO UPDATE SET " +
                    "temperature = EXCLUDED.temperature, " +
                    "wind_speed = EXCLUDED.wind_speed, " +
                    "humidity = EXCLUDED.humidity, " +
                    "pop = EXCLUDED.pop, " +
                    "clouds_all = EXCLUDED.clouds_all";

            for (Weather weather : weatherList) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                    preparedStatement.setString(1, weather.getDtTxt());
                    preparedStatement.setDouble(2, weather.getTemp());
                    preparedStatement.setDouble(3, weather.getSpeed());
                    preparedStatement.setInt(4, weather.getHumidity());
                    preparedStatement.setDouble(5, weather.getPop());
                    preparedStatement.setInt(6, weather.getCloudsAll());
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public Connection connect(String dbPath) {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:" + dbPath;
            conn = DriverManager.getConnection(url);
            return conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


}
