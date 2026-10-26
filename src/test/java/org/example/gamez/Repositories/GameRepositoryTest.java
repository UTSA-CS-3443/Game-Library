package org.example.gamez.Repositories;

import org.example.gamez.Infrastructure.Database;
import org.example.gamez.Models.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GameRepositoryTest {
    private GameRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        Database.setUrl("jdbc:sqlite::memory:");
        Database.migrate();
        repository = new GameRepository();
    }

    @Test
    void insertFindUpdateAndDeleteGame() throws SQLException {
        Game game = new Game("Test Game", "Description", "Jaxon", "C:\\Games\\Test", "cover.png");

        int id = repository.insert(game);
        assertTrue(id > 0);

        Game found = repository.findById(id).orElseThrow();
        assertEquals("Test Game", found.getTitle());
        assertEquals("Jaxon", found.getAuthor());
        assertEquals("cover.png", found.getImagePath());

        found.setTitle("Updated Game");
        assertTrue(repository.update(found));
        assertEquals("Updated Game", repository.findById(id).orElseThrow().getTitle());

        assertFalse(repository.update(new Game("Missing", "", "", "", "")));

        assertTrue(repository.delete(id));
        assertTrue(repository.findById(id).isEmpty());
        assertFalse(repository.delete(id));
        assertTrue(repository.findAll().isEmpty());
    }
}