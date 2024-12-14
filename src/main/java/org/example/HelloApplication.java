package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.controller.EventController;
import org.example.controller.LoginFormController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;


public class HelloApplication extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws Exception {
        Properties config = loadProperties("application.properties");

        String dbUrl = config.getProperty("db.url");
        String dbUser = config.getProperty("db.user");
        String dbPassword = config.getProperty("db.password");

        EventController eventController = new EventController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login_form.fxml"));
        Parent root = loader.load();

        root.setStyle(".root");

        enableWindowDragging(stage, root);

        LoginFormController loginFormController = loader.getController();
        loginFormController.setEventController(eventController);
        loginFormController.initializeResources(dbUrl, dbUser, dbPassword);
        Scene scene = new Scene(root, 500, 600);
        scene.setFill(Color.TRANSPARENT);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/login_view_style.css")).toExternalForm());

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setTitle("Login Application");
        stage.show();
    }

    /**
     * Enables dragging of the custom transparent window.
     */
    private void enableWindowDragging(Stage stage, Parent root) {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    private Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IOException("Unable to find " + fileName);
            }
            properties.load(input);
        }
        return properties;
    }

    public static void main(String[] args) {
        launch(args);
    }
}