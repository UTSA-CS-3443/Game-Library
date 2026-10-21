package org.example.gamez.Entrypoints;


import org.example.gamez.Infrastructure.Database;

public class ConsoleApplication {

    public static void main(String[] args) {

        try {
            Database.migrate();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
