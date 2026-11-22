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

        conn.setAutoCommit(false);
        try (Statement stmt = conn.createStatement()) {
            boolean gamesTableExists;
            try (var tables = stmt.executeQuery(
                    "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = 'games'")) {
                gamesTableExists = tables.next();
            }

            for (String migration : migrations) {
                stmt.execute(migration);
            }

            if (!gamesTableExists && !url.equals("jdbc:sqlite::memory:")) {
                stmt.executeUpdate("""
                INSERT INTO games (title, description, location, image_path)
                VALUES
                    ('Pong', 'A two-player paddle game.',
                     'data/games/pong.exe', 'data/images/pong.png'),
                    ('Number Merge', 'Slide and combine matching tiles to reach 2048.',
                     'data/games/number-merge.exe', 'data/images/number-merge.png'),
                    ('Asteroid Field', 'Pilot a ship through a field of asteroids.',
                     'data/games/asteroid-field.exe', 'data/images/asteroid-field.png'),
                    ('Breakout', 'Bounce the ball off your paddle to clear the bricks.',
                     'data/games/breakout.exe', 'data/images/breakout.png'),
                    ('Connect Four', 'Drop pieces into columns and line up four in a row.',
                     'data/games/connect-four.exe', 'data/images/connect-four.png'),
                    ('Memory Match', 'Flip cards and find all the matching pairs.',
                     'data/games/memory-match.exe', 'data/images/memory-match.png'),
                    ('Minesweeper', 'Use number clues to clear the board without hitting a mine.',
                     'data/games/minesweeper.exe', 'data/images/minesweeper.png'),
                    ('Snake', 'Collect food and grow while avoiding your own tail.',
                     'data/games/snake.exe', 'data/images/snake.png'),
                    ('Falling Blocks', 'Arrange falling pieces to clear full rows.',
                     'data/games/falling-blocks.exe', 'data/images/falling-blocks.png'),
                    ('Tic-Tac-Toe', 'Place three matching marks in a row to win.',
                     'data/games/tic-tac-toe.exe', 'data/images/tic-tac-toe.png')
                """);
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
        System.out.println("Database migration check passed successfully.");
    }
}