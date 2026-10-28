package org.example.gamez.Controllers;


import org.example.gamez.Models.Game;
import org.example.gamez.Services.GameService;


import java.util.List;
import java.util.Scanner;


public class GameController {


    private final GameService gameService;
    private final Scanner scanner;


    public GameController(GameService gameService, Scanner scanner) {
        this.gameService = gameService;
        this.scanner = scanner;
    }


    public void run() {
        boolean running = true;


        while (running) {
            printMenu();


            int choice = readInt("Choose an option: ");


            try {
                switch (choice) {
                    case 1 -> listGames();
                    case 2 -> viewGame();
                    case 3 -> addGame();
                    case 4 -> updateGame();
                    case 5 -> deleteGame();
                    case 6 -> launchGame();
                    case 0 -> {
                        System.out.println("Exiting...");
                        running = false;
                    }
                    default -> System.out.println("Invalid option. Please choose 0-6.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    private void printMenu() {
        System.out.println();
        System.out.println("===== GAME LIBRARY =====");
        System.out.println("1. List games");
        System.out.println("2. View game details");
        System.out.println("3. Add game");
        System.out.println("4. Update game");
        System.out.println("5. Delete game");
        System.out.println("6. Launch game");
        System.out.println("0. Exit");
        System.out.println("========================");
    }


    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();


            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }


    private String readRequired(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();


            if (!input.isEmpty()) {
                return input;
            }


            System.out.println("This value is required.");
        }
    }


    private String readOptional(String prompt, String currentValue) {
        System.out.print(prompt + " [" + currentValue + "]: ");
        String input = scanner.nextLine().trim();


        if (input.isEmpty()) {
            return currentValue;
        }


        return input;
    }


    private void listGames() {
        List<Game> games = gameService.listGames();


        if (games.isEmpty()) {
            System.out.println("No games found.");
            return;
        }


        System.out.printf("%-5s %-30s%n", "ID", "Title");
        System.out.println("-----------------------------------");


        for (Game game : games) {
            System.out.printf("%-5d %-30s%n",
                    game.getId(),
                    game.getTitle());
        }
    }


    private void viewGame() {
        int id = readInt("Enter game ID: ");


        Game game = gameService.getGame(id);


        printGame(game);
    }


    private void printGame(Game game) {
        System.out.println();
        System.out.println("ID:          " + game.getId());
        System.out.println("Title:       " + game.getTitle());
        System.out.println("Description: " + game.getDescription());
        System.out.println("Author:      " + game.getAuthor());
        System.out.println("Location:    " + game.getLocation());
        System.out.println("Image Path:  " + game.getImagePath());
    }


    private void addGame() {
        String title = readRequired("Title: ");
        String description = readRequired("Description: ");
        String author = readRequired("Author: ");
        String location = readRequired("Location: ");
        String imagePath = readRequired("Image path: ");


        Game game = new Game(
                title,
                description,
                author,
                location,
                imagePath
        );


        Game addedGame = gameService.addGame(game);


        System.out.println(
                "Game added successfully with ID " + addedGame.getId() + "."
        );
    }


    private void updateGame() {
        int id = readInt("Enter game ID to update: ");


        Game game = gameService.getGame(id);


        System.out.println(
                "Press Enter without typing anything to keep the current value."
        );


        game.setTitle(
                readOptional("Title", game.getTitle())
        );


        game.setDescription(
                readOptional("Description", game.getDescription())
        );


        game.setAuthor(
                readOptional("Author", game.getAuthor())
        );


        game.setLocation(
                readOptional("Location", game.getLocation())
        );


        game.setImagePath(
                readOptional("Image path", game.getImagePath())
        );


        gameService.updateGame(game);


        System.out.println("Game updated successfully.");
    }


    private void deleteGame() {
        int id = readInt("Enter game ID to delete: ");


        Game game = gameService.getGame(id);


        System.out.println(
                "You are about to delete: " + game.getTitle()
        );


        System.out.print("Are you sure? (y/n): ");
        String confirmation = scanner.nextLine().trim();


        if (confirmation.equalsIgnoreCase("y")) {
            gameService.deleteGame(id);
            System.out.println("Game deleted successfully.");
        } else {
            System.out.println("Delete cancelled.");
        }
    }


    private void launchGame() {
        int id = readInt("Enter game ID to launch: ");


        gameService.launchGame(id);


        System.out.println("Game launched successfully.");
    }
}

