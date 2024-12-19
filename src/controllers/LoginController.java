package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DatabaseHelper;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private static int loggedInUserId; // Store logged-in user's ID

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    @FXML
    private void onLoginClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        int userId = DatabaseHelper.authenticateUser(username, password);
        if (userId != -1) {
            LoginController.loggedInUserId = userId; // Set the logged-in user ID
            System.out.println("Login successful!");

            // Transition to the dashboard
            try {
                Stage stage = (Stage) usernameField.getScene().getWindow(); // Get the current stage
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard.fxml"));
                Scene dashboardScene = new Scene(loader.load());
                stage.setScene(dashboardScene);
                stage.setTitle("Finance Tracker - Dashboard");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid username or password.");
        }
    }


    @FXML
    private void onRegisterClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        boolean success = DatabaseHelper.registerUser(username, password);
        if (success) {
            System.out.println("Registration successful! You can now log in.");
        } else {
            System.out.println("Username already exists. Please choose another.");
        }
    }
}
