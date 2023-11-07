package org.ulpc.dacd.control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteWeatherStore {

    public void createTable(String tableName) {
        try (Connection connection = connect("weather.db")) {
            Statement statement = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "id INTEGER PRIMARY KEY,\n" +
                    "name TEXT NOT NULL,\n" +
                    "temperature REAL DEFAULT 0," +
                    "wind_speed REAL DEFAULT 0," +
                    "humidity INTEGER DEFAULT 0" +
                    ");";
            statement.execute(createTableSQL);
            System.out.println("Table '" + tableName + "' has been created.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public Connection connect(String dbPath) {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:" + dbPath;
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            return conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
