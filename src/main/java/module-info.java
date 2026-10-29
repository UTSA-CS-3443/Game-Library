module org.example.gamez {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    exports org.example.gamez.Entrypoints;
    exports org.example.gamez.Controllers;
    exports org.example.gamez.Models;

    opens org.example.gamez.Entrypoints to javafx.graphics;

    opens org.example.gamez.Controllers to javafx.fxml;
}
