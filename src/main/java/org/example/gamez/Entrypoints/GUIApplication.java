package org.example.gamez.Entrypoints;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.gamez.Infrastructure.Database;
import org.example.gamez.Repositories.GameRepository;
import org.example.gamez.Services.GameService;

import java.io.IOException;
import java.io.UncheckedIOException;


public class GUIApplication extends Application {

    private static final String BASE = "/org/example/gamez/";
    private static final String[] FONTS = {
            "Inter-Regular.ttf", "Inter-Medium.ttf", "Inter-SemiBold.ttf", "SpaceGrotesk-Bold.ttf"
    };

    private static Stage stage;
    private static GameService service;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Database.migrate();
        service = new GameService(new GameRepository());
        for (String font : FONTS) {
            Font.loadFont(GUIApplication.class.getResourceAsStream(BASE + "fonts/" + font), 14);
        }

        stage = primaryStage;
        Scene scene = new Scene(new Pane(), 960, 620, Color.web("#090B0F"));
        stage.setScene(scene);
        stage.setTitle("GameZ");
        stage.setMinWidth(960);
        stage.setMinHeight(620);
        navigate("home.fxml");
        stage.show();
    }

    public static GameService getService() {
        return service;
    }

    public static <T> T navigate(String view) {
        FXMLLoader loader = new FXMLLoader(GUIApplication.class.getResource(BASE + "views/" + view));
        try {
            Parent root = loader.load();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load view " + view, e);
        }
        return loader.getController();
    }
}
