package org.example.gamez.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.example.gamez.Entrypoints.GUIApplication;
import org.example.gamez.Models.Game;
import org.example.gamez.Services.GameService;

import java.io.File;

public class EditController {

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField authorField;
    @FXML private Label errorLabel;
    @FXML private Button submitButton;
    @FXML private TextField selectedLocation;
    @FXML private TextField selectedImage;



    private final GameService service = GUIApplication.getService();
    private Game game;

    public void setGame(Game game) {
        this.game = game;
        if (game == null) {
            return;
        }
        titleLabel.setText("Update Game");
        subtitleLabel.setText("Edit the selected game's information.");
        submitButton.setText("Update Game");
        titleField.setText(game.getTitle());
        descriptionField.setText(game.getDescription());
        authorField.setText(game.getAuthor());
        selectedLocation.setText(game.getLocation());
        selectedImage.setText(game.getImagePath());
    }

    @FXML
    private void onSubmit() {
        Game form = new Game(titleField.getText(), descriptionField.getText(), authorField.getText(),
                selectedLocation.getText(), selectedImage.getText());

        try {
            if (game == null) {
                form = service.addGame(form);
            } else {
                form.setId(game.getId());
                service.updateGame(form);
            }
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setManaged(true);
            errorLabel.setVisible(true);
            return;
        }
        backToLibrary(form.getId());
    }

    @FXML
    private void onBack() {
        backToLibrary(game == null ? 0 : game.getId());
    }

    @FXML
    private void onBrowseLocation() {
        browse(selectedLocation, "Select a game executable", "data/games",
                new FileChooser.ExtensionFilter("Windows executables", "*.exe"));
    }

    @FXML
    private void onBrowseImage() {
        browse(selectedImage, "Select a cover image", "data/images",
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
    }

    private void browse(TextField pathLabel, String title, String defaultFolder,
                        FileChooser.ExtensionFilter filter) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(filter);

        File directory = new File(defaultFolder).getAbsoluteFile();
        String currentPath = pathLabel.getText();

        if (currentPath != null && !currentPath.isBlank()) {
            File parent = new File(currentPath.trim()).getAbsoluteFile().getParentFile();
            if (parent != null && parent.isDirectory()) {
                directory = parent;
            }
        }

        if (directory.isDirectory()) {
            chooser.setInitialDirectory(directory);
        }

        File chosen = chooser.showOpenDialog(GUIApplication.getStage());
        if (chosen != null) {
            pathLabel.setText(chosen.getAbsolutePath());
        }
    }

    private void backToLibrary(int gameId) {
        HomeController home = GUIApplication.navigate("home.fxml");
        home.select(gameId);
    }
}
