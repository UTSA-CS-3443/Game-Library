package org.example.gamez.Entrypoints;


import org.example.gamez.Controllers.GameController;
import org.example.gamez.Infrastructure.Database;
import org.example.gamez.Repositories.GameRepository;
import org.example.gamez.Services.GameService;

import java.util.Scanner;

/**
 * Starts the console version of GameZ: prepares the database,
 * wires repository, service and controller together, and runs the menu.
 */
public class ConsoleApplication {

    /**
     * @param args not used
     */
    public static void main(String[] args) {

        try {
            Database.migrate();

            GameRepository repository = new GameRepository();
            GameService service = new GameService(repository);
            Scanner scanner = new Scanner(System.in);
            GameController controller = new GameController(service, scanner);

            controller.run();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
