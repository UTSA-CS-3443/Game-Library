package org.example.gamez.Repositories;

import org.example.gamez.Infrastructure.Database;
import org.example.gamez.Models.Game;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Loads and saves games in the database.
 * This is the only class that runs SQL; every query uses a PreparedStatement.
 */
public class GameRepository {

    private Connection database;

    /**
     * Turns the current row of a query result into a Game.
     *
     * @param row a result set positioned on a row of the games table
     * @return the game in that row
     */
    private Game mapRow(ResultSet row) throws SQLException {
        Game game = new Game(
                row.getString("title"),
                row.getString("description"),
                row.getString("author"),
                row.getString("location"),
                row.getString("image_path")
        );
        game.setId(row.getInt("id"));
        return game;
    }

    /**
     * Opens (or reuses) the shared database connection.
     *
     * @throws SQLException if the database can't be reached
     */
    public GameRepository() throws SQLException {
        database = Database.getConnection();
    }

    /**
     * @return every game in the library; an empty list if there are none
     * @throws SQLException if the query fails
     */
    public List<Game> findAll() throws SQLException {
        String sql = "SELECT id, title, description, author, location, image_path FROM games";
        List<Game> games = new ArrayList<>();

        try (PreparedStatement statement = database.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    games.add(mapRow(rows));
                }
        }

        return games;
    }

    /**
     * @param id the game's id
     * @return the game, or an empty Optional if no game has that id
     * @throws SQLException if the query fails
     */
    public Optional<Game> findById(int id) throws SQLException {
        String sql = "SELECT id, title, description, author, location, image_path FROM games WHERE id = ?";

        try (PreparedStatement statement = database.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet row = statement.executeQuery()) {
                if (row.next()) {
                    return Optional.of(mapRow(row));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Saves a new game.
     *
     * @param game the game to save; its id is ignored
     * @return the id the database generated for it
     * @throws SQLException if the insert fails
     */
    public int insert(Game game) throws SQLException {
        String sql = "INSERT INTO games (title, description, author, location, image_path) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = database.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, game.getTitle());
            statement.setString(2, game.getDescription());
            statement.setString(3, game.getAuthor());
            statement.setString(4, game.getLocation());
            statement.setString(5, game.getImagePath());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("Inserting the game did not return a generated ID.");
            }
        }
    }

    /**
     * Saves changes to an existing game, matched by its id.
     *
     * @param game the game with its updated fields
     * @return true if a game was updated, false if no game had that id
     * @throws SQLException if the update fails
     */
    public boolean update(Game game) throws SQLException {
        String sql = "UPDATE games SET title = ?, description = ?, author = ?, "
                + "location = ?, image_path = ? WHERE id = ?";

        try (PreparedStatement statement = database.prepareStatement(sql)) {
            statement.setString(1, game.getTitle());
            statement.setString(2, game.getDescription());
            statement.setString(3, game.getAuthor());
            statement.setString(4, game.getLocation());
            statement.setString(5, game.getImagePath());
            statement.setInt(6, game.getId());

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * @param id the id of the game to remove
     * @return true if a game was deleted, false if no game had that id
     * @throws SQLException if the delete fails
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM games WHERE id = ?";

        try (PreparedStatement statement = database.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }
}
