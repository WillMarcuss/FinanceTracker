package controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.DatabaseHelper;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private VBox loginBox; // Correctly linked to the VBox in FXML
    @FXML private Label errorLabel;

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
            errorLabel.setText("Invalid username or password.");
            applyShakeEffect(loginBox); // Shake the login box on failure
        }
    }

    private void applyShakeEffect(VBox node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(100), node);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(4);
        shake.setAutoReverse(true);
        shake.play();
    }

    @FXML
    private void onRegisterClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill out all fields.");
            applyShakeEffect(loginBox); // Shake the login box on empty fields
            return;
        }

        boolean success = DatabaseHelper.registerUser(username, password);
        if (success) {
            errorLabel.setText("Registration successful! You can now log in.");
        } else {
            errorLabel.setText("Username already exists. Please choose another.");
            applyShakeEffect(loginBox); // Shake the login box on username conflict
        }
    }
}
