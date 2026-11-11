package org.example.gamez.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.gamez.Entrypoints.GUIApplication;
import org.example.gamez.Models.Game;
import org.example.gamez.Services.GameService;

public class EditController {

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField authorField;
    @FXML private TextField locationField;
    @FXML private TextField imageField;
    @FXML private Label errorLabel;
    @FXML private Button submitButton;

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
        locationField.setText(game.getLocation());
        imageField.setText(game.getImagePath());
    }

    @FXML
    private void onSubmit() {
        Game form = new Game(titleField.getText(), descriptionField.getText(), authorField.getText(),
                locationField.getText(), imageField.getText());
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

    private void backToLibrary(int gameId) {
        HomeController home = GUIApplication.navigate("home.fxml");
        home.select(gameId);
    }
}
