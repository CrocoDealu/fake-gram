module com.example.lab6fx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;
    requires jdk.compiler;

    opens org.example to javafx.fxml;
    exports org.example;
    exports org.example.controller;
    opens org.example.controller to javafx.fxml;
}