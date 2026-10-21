package org.example.gamez.Models;

/**
 * Game data structure
 */
public class Game {
    private int id;
    private String title;
    private String description;
    private String author;
    private String location;
    private String imagePath;

    public Game() {}

    public Game(String title, String description, String author, String location, String imagePath) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.location = location;
        this.imagePath = imagePath;
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
