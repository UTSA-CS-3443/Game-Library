package org.example.gamez.Controllers;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.example.gamez.Entrypoints.GUIApplication;
import org.example.gamez.Models.Game;
import org.example.gamez.Services.GameService;

import java.io.File;

/**
 * Library screen: lists games, shows the selected one, and runs
 * the play, add, update and delete actions.
 */
public class HomeController {

    private static final String DEFAULT_SUBTITLE = "Select a game, then choose an action.";

    @FXML private Label subtitleLabel;
    @FXML private VBox libraryList;
    @FXML private Label cardTitleLabel;
    @FXML private StackPane imageFrame;
    @FXML private ImageView coverImage;
    @FXML private Button playButton;
    @FXML private Button exitButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private StackPane deleteOverlay;
    @FXML private Label deleteMessageLabel;

    private final GameService service = GUIApplication.getService();
    private final ToggleGroup libraryGroup = new ToggleGroup();
    private Game selected;

    @FXML
    private void initialize() {
        Rectangle clip = new Rectangle(364, 218);
        clip.setArcWidth(22);
        clip.setArcHeight(22);
        coverImage.setClip(clip);

        libraryGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                if (oldToggle != null) {
                    libraryGroup.selectToggle(oldToggle);
                }
                return;
            }
            selected = (Game) newToggle.getUserData();
            showSelected();
        });

        loadLibrary();
        showEmpty();
    }

    /**
     * Selects the game with this id, if it is in the library.
     */
    public void select(int gameId) {
        libraryGroup.getToggles().stream()
                .filter(t -> ((Game) t.getUserData()).getId() == gameId)
                .findFirst()
                .ifPresent(libraryGroup::selectToggle);
    }

    private void loadLibrary() {
        libraryList.getChildren().clear();
        for (Game game : service.listGames()) {
            ToggleButton item = new ToggleButton(game.getTitle());
            item.getStyleClass().setAll("library-item");
            item.setUserData(game);
            item.setToggleGroup(libraryGroup);
            libraryList.getChildren().add(item);
        }
    }

    private void showEmpty() {
        selected = null;
        setSubtitle(DEFAULT_SUBTITLE, false);
        cardTitleLabel.setText("Select Game");
        show(imageFrame, false);
        show(playButton, true);
        show(exitButton, false);
        playButton.setDisable(true);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void showSelected() {
        setSubtitle(DEFAULT_SUBTITLE, false);
        cardTitleLabel.setText(selected.getTitle());
        show(imageFrame, false);
        show(playButton, true);
        show(exitButton, false);
        playButton.setDisable(false);
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    private void showPlaying() {
        setSubtitle("Game is currently playing.", false);
        setCover(selected.getImagePath());
        show(imageFrame, true);
        show(playButton, false);
        show(exitButton, true);
    }

    private void setCover(String path) {
        File file = path == null ? null : new File(path);
        if (file == null || !file.exists()) {
            coverImage.setImage(null);
            return;
        }
        Image image = new Image(file.toURI().toString());
        coverImage.setImage(image);
        coverImage.setViewport(coverViewport(image, coverImage.getFitWidth(), coverImage.getFitHeight()));
    }

    private static Rectangle2D coverViewport(Image image, double width, double height) {
        double scale = Math.max(width / image.getWidth(), height / image.getHeight());
        double w = width / scale;
        double h = height / scale;
        return new Rectangle2D((image.getWidth() - w) / 2, (image.getHeight() - h) / 2, w, h);
    }

    private void setSubtitle(String text, boolean error) {
        subtitleLabel.setText(text);
        subtitleLabel.getStyleClass().remove("error");
        if (error) {
            subtitleLabel.getStyleClass().add("error");
        }
    }

    private static void show(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    @FXML
    private void onPlay() {
        try {
            service.launchGame(selected.getId());
            showPlaying();
        } catch (RuntimeException e) {
            setSubtitle(e.getMessage(), true);
        }
    }

    @FXML
    private void onExit() {
        showSelected();
    }

    @FXML
    private void onAdd() {
        EditController edit = GUIApplication.navigate("edit.fxml");
        edit.setGame(null);
    }

    @FXML
    private void onUpdate() {
        EditController edit = GUIApplication.navigate("edit.fxml");
        edit.setGame(selected);
    }

    @FXML
    private void onDelete() {
        deleteMessageLabel.setText("Are you sure you want to delete " + selected.getTitle() + "?");
        setSubtitle("Confirm deletion of the selected game.", false);
        deleteOverlay.setVisible(true);
    }

    @FXML
    private void onCancelDelete() {
        deleteOverlay.setVisible(false);
        setSubtitle(DEFAULT_SUBTITLE, false);
    }

    @FXML
    private void onConfirmDelete() {
        deleteOverlay.setVisible(false);
        try {
            service.deleteGame(selected.getId());
            loadLibrary();
            showEmpty();
        } catch (RuntimeException e) {
            setSubtitle(e.getMessage(), true);
        }
    }
}
