package org.example.gamez.Services;

import org.example.gamez.Models.Game;
import org.example.gamez.Repositories.GameRepository;

import java.util.List;

/**
 * Handles bussiness logic
 */
public class GameService {

    private final GameRepository repository;

    public GameService(GameRepository repository) {
        this.repository = repository;
    }

    public List<Game> listGames() {
        throw new UnsupportedOperationException("TODO (Luke): listGames");
    }

    /**
     * @throws IllegalArgumentException if not found
     */
    public Game getGame(int id) {
        throw new UnsupportedOperationException("TODO (Luke): getGame");
    }

    /**
     * Validates the game and saves it.
     * @return the game with its new id
     */
    public Game addGame(Game game) {
        throw new UnsupportedOperationException("TODO (Luke): addGame");
    }

    /**
     * @throws IllegalArgumentException if invalid or not found
     */
    public void updateGame(Game game) {
        throw new UnsupportedOperationException("TODO (Luke): updateGame");
    }

    /**
     * @throws IllegalArgumentException if not found
     */
    public void deleteGame(int id) {
        throw new UnsupportedOperationException("TODO (Luke): deleteGame");
    }

    /**
     * @throws IllegalStateException if the file is missing or can't be opened
     */
    public void launchGame(int id) {
        throw new UnsupportedOperationException("TODO (Luke): launchGame");
    }
}
