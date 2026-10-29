package org.example.gamez.Services;

import org.example.gamez.Infrastructure.Database;
import org.example.gamez.Models.Game;
import org.example.gamez.Repositories.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private GameService service;

    @BeforeEach
    void setUp() throws SQLException {
        // Fresh, empty in-memory database before every test
        Database.setUrl("jdbc:sqlite::memory:");
        Database.migrate();
        service = new GameService(new GameRepository());
    }

    @Test
    void addGameTrimsFieldsAndAssignsId() {
        Game added = service.addGame(new Game("  Zelda  ", " desc ", "Nintendo", " C:\\zelda.exe ", null));

        assertTrue(added.getId() > 0);
        Game saved = service.getGame(added.getId());
        assertEquals("Zelda", saved.getTitle());
        assertEquals("desc", saved.getDescription());
        assertEquals("C:\\zelda.exe", saved.getLocation());
    }

    @Test
    void addGameWithBlankTitleThrows() {
        Game game = new Game("   ", "desc", "me", "C:\\game.exe", null);
        assertThrows(IllegalArgumentException.class, () -> service.addGame(game));
    }

    @Test
    void addGameWithNullLocationThrows() {
        Game game = new Game("Zelda", "desc", "me", null, null);
        assertThrows(IllegalArgumentException.class, () -> service.addGame(game));
    }

    @Test
    void addGameAllowsEmptyOptionalFields() {
        Game added = service.addGame(new Game("Zelda", null, null, "C:\\zelda.exe", null));
        assertTrue(added.getId() > 0);
    }

    @Test
    void getGameWithMissingIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> service.getGame(999));
    }

    @Test
    void updateGameWithMissingIdThrows() {
        Game game = new Game("Zelda", "desc", "me", "C:\\zelda.exe", null);
        game.setId(999);
        assertThrows(IllegalArgumentException.class, () -> service.updateGame(game));
    }

    @Test
    void deleteGameRemovesIt() {
        Game added = service.addGame(new Game("Zelda", null, null, "C:\\zelda.exe", null));

        service.deleteGame(added.getId());

        assertTrue(service.listGames().isEmpty());
    }

    @Test
    void deleteGameWithMissingIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteGame(999));
    }

    @Test
    void launchGameWithMissingFileThrows() {
        Game added = service.addGame(new Game("Ghost", null, null, "C:\\does\\not\\exist.exe", null));
        assertThrows(IllegalStateException.class, () -> service.launchGame(added.getId()));
    }
}