package org.example.gamez.Infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Database{
    private static Connection connection;
    private static String url = "jdbc:sqlite:data/gamez.db";

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url);
        }

        return connection;
    }

    public static void setUrl(String newUrl) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        connection = null;
        url = newUrl;
    }


    public static void migrate() throws SQLException {
        Connection conn = getConnection();

        String[] migrations = {
                "DROP TABLE IF EXISTS test;",
                "CREATE TABLE IF NOT EXISTS games (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT NOT NULL, " +
                        "description TEXT, " +
                        "author TEXT, " +
                        "location TEXT NOT NULL, " +
                        "image_path TEXT" +
                        ");"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String migration : migrations) {
                stmt.execute(migration);
            }
        }
        System.out.println("Database migration check passed successfully.");
    }
}