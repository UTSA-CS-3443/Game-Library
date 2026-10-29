package org.example.gamez.Models;

/**
 * One game in the user's library.
 * Plain data only: no database or user interface code.
 * Title and location are required; description, author and imagePath are optional.
 */
public class Game {
    private int id;
    private String title;
    private String description;
    private String author;
    private String location;
    private String imagePath;

    /**
     * Creates an empty game. Fields are filled in with the setters.
     */
    public Game() {}

    /**
     * Creates a game that hasn't been saved yet. The id is set later,
     * once the database generates it.
     *
     * @param title       the game's name
     * @param description a short description, or null
     * @param author      the developer or publisher, or null
     * @param location    the path to the game's file on disk
     * @param imagePath   the path to the cover image, or null
     */
    public Game(String title, String description, String author, String location, String imagePath) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.location = location;
        this.imagePath = imagePath;
    }

    /** @return the game's database id, or 0 if it hasn't been saved yet */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    /** @return the path to the game's file, used to launch it */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /** @return the path to the game's cover image, or null */
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
