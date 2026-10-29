package org.example.gamez.Services;

import org.example.gamez.Models.Game;
import org.example.gamez.Repositories.GameRepository;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.awt.Desktop;

/**
 * Business logic for games: validates input, reports games that don't exist,
 * and launches game files. Sits between GameController and GameRepository.
 */
public class GameService {

    private final GameRepository repository;

    /**
     * @param repository where games are loaded from and saved to
     */
    public GameService(GameRepository repository) {
        this.repository = repository;
    }

    /**
     * @return a list of all games
     */
    public List<Game> listGames() {

        try {

            // Call Repository
            return repository.findAll();

        } catch (SQLException e) {
            throw new RuntimeException("Could not load games", e);
        }
    }

    /**
     * @return Game with correct id
     * @throws IllegalArgumentException if not found
     */
    public Game getGame(int id) {

        try {

            // Call on repository
            Optional<Game> game = repository.findById(id);
            if (game.isEmpty()) {
                throw new IllegalArgumentException("Game with id " + id + " not found");
            }
            // Return the contents of the game
            return game.get();
        } catch (SQLException e) {
            throw new RuntimeException("Could not load game " + id, e);
        }
    }

    /**
     * Validates the game and saves it.
     * @return the game with its new id
     */
    public Game addGame(Game game) {

        // Check if game has correct information
        validateGame(game);

        // Try to add game
        try {
            game.setId(repository.insert(game));
        } catch (SQLException e) {
            throw new RuntimeException("Could not insert game", e);
        }

        return game;
    }

    /**
     * @throws IllegalArgumentException if invalid or not found
     */
    public void updateGame(Game game) {

        // Check if game has correct information
        validateGame(game);

        // Try to update game
        try {
            if (!repository.update(game)) {  // Checking if false
                throw new IllegalArgumentException("Game with id " + game.getId() + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not update game", e);
        }
    }

    /**
     * @throws IllegalArgumentException if not found
     */
    public void deleteGame(int id) {

        try {
            if (!repository.delete(id)) {  // Checking if false
                throw new IllegalArgumentException("Game with id " + id + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete game", e);
        }
    }

    /**
     * @throws IllegalStateException if the file is missing or can't be opened
     */
    public void launchGame(int id) {

        Game game = getGame(id);
        File file = new File(game.getLocation());

        // Check if file exists
        if  (!file.exists()) {
            throw new IllegalStateException("Game file not found at " + game.getLocation());
        }

        // Ask OS to open the file
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            throw new IllegalStateException("Could not launch game: " + e.getMessage(), e);
        }

    }

    /**
     * Trims the game's text fields and checks that the required ones are present.
     *
     * @param game the game to clean up and check
     * @throws IllegalArgumentException if the title or location is missing or blank
     */
    private void validateGame(Game game) {

        game.setTitle(trimOrNull(game.getTitle()));
        if (game.getTitle() == null || game.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot null or empty");
        }
        game.setLocation(trimOrNull(game.getLocation()));
        if (game.getLocation() == null || game.getLocation().isEmpty()) {
            throw new IllegalArgumentException("Location cannot null or empty");
        }

        game.setDescription(trimOrNull(game.getDescription()));
        game.setAuthor(trimOrNull(game.getAuthor()));
        game.setImagePath(trimOrNull(game.getImagePath()));
    }

    /**
     * @return the value with surrounding spaces removed, or null if it was null
     */
    private String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }
}
