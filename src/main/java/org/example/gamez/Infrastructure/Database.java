package org.example.gamez.Infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Database{
    private static Connection connection;
    private static final String URL = "jdbc:sqlite:data/gamez.db";

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
        }

        return connection;
    }


    public static void migrate() throws SQLException {
        Connection conn = getConnection();

        String migrationQuery = "CREATE TABLE IF NOT EXISTS test (" +
                "id INTEGER NOT NULL PRIMARY KEY, " +
                "name TEXT" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(migrationQuery);
        }
        System.out.println("Database migration check passed successfully.");
    }
}