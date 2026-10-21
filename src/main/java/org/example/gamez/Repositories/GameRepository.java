package org.example.gamez.Repositories;

import javafx.scene.chart.PieChart;
import org.example.gamez.Infrastructure.Database;

import java.sql.Connection;
import java.sql.SQLException;

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


    public void test() {

    }
}
