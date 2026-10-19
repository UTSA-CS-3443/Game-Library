module org.example.gamez {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.gamez to javafx.fxml;
    exports org.example.gamez;
}