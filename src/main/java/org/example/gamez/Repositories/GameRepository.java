package org.example.gamez.Repositories;

import org.example.gamez.Infrastructure.Database;
import org.example.gamez.Models.Game;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Handles data persistence
 * mapping of Game -> SQL
 * ALL DB ACCESS SHOULD LIVE HERE!
 */
public class GameRepository {

    private Connection database;

    public GameRepository() throws SQLException {
        database = Database.getConnection();
    }

    public List<Game> findAll() throws SQLException {
        throw new UnsupportedOperationException("TODO (Jaxon): findAll");
    }

    public Optional<Game> findById(int id) throws SQLException {
        throw new UnsupportedOperationException("TODO (Jaxon): findById");
    }

    /**
     * @return the generated id
     */
    public int insert(Game game) throws SQLException {
        throw new UnsupportedOperationException("TODO (Jaxon): insert");
    }

    /**
     * @return false if no row had that id
     */
    public boolean update(Game game) throws SQLException {
        throw new UnsupportedOperationException("TODO (Jaxon): update");
    }

    /**
     * @return false if no row had that id
     */
    public boolean delete(int id) throws SQLException {
        throw new UnsupportedOperationException("TODO (Jaxon): delete");
    }
}
