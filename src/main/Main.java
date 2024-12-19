package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.DatabaseHelper;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize the database
        DatabaseHelper.initializeDatabase(); // Ensure database tables exist

        // Show the login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
        Scene loginScene = new Scene(loader.load());
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login");
        primaryStage.show(); // Use show() instead of showAndWait()

        // You will need to transition to the dashboard after login is successful
    }

    public static void main(String[] args) {
        launch(args);
    }
}
