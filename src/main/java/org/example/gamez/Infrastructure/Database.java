package org.example.gamez.Infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the app's single SQLite connection and creates the tables it needs.
 * Abstract and static-only: it is never instantiated.
 */
public abstract class Database{
    private static Connection connection;
    private static String url = "jdbc:sqlite:data/gamez.db";

    /**
     * @return the shared connection, opening it first if needed
     * @throws SQLException if the database can't be opened
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url);
        }

        return connection;
    }

    /**
     * Switches to a different database, closing the current connection.
     * Tests use this to run against an in-memory database.
     *
     * @param newUrl a JDBC URL, e.g. "jdbc:sqlite::memory:"
     * @throws SQLException if the old connection can't be closed
     */
    public static void setUrl(String newUrl) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        connection = null;
        url = newUrl;
    }

    /**
     * Creates the games table if it doesn't exist yet. Safe to run on every startup.
     *
     * @throws SQLException if a statement fails
     */
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