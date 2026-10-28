package org.example.gamez.Entrypoints;


import org.example.gamez.Controllers.GameController;
import org.example.gamez.Infrastructure.Database;
import org.example.gamez.Repositories.GameRepository;
import org.example.gamez.Services.GameService;

import java.util.Scanner;

public class ConsoleApplication {

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
