package org.ulpc.dacd.control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteWeatherStore {
    public static void main(String[] args) {
        String dbPath = "example.db";
        SqliteWeatherStore sqliteWeatherStore = new SqliteWeatherStore();

        try (Connection connection = sqliteWeatherStore.connect(dbPath)) {
            Statement statement = connection.createStatement();
            sqliteWeatherStore.createTable(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY,\n" +
                "name TEXT NOT NULL,\n" +
                "price REAL DEFAULT 0" +
                ");");
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
