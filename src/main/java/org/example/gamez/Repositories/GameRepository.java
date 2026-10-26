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
 * Handles data persistence
 * mapping of Game -> SQL
 * ALL DB ACCESS SHOULD LIVE HERE!
 */
public class GameRepository {

    private Connection database;

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

    public GameRepository() throws SQLException {
        database = Database.getConnection();
    }

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
     * @return the generated id
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
     * @return false if no row had that id
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
     * @return false if no row had that id
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM games WHERE id = ?";

        try (PreparedStatement statement = database.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }
}
