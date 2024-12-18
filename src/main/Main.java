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
        DatabaseHelper.initializeDatabase();

        // Load the main dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Personal Finance Tracker");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
