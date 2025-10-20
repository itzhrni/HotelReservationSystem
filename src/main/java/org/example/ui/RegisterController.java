package org.example.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.model.Guest;
import org.example.service.GuestService;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Hyperlink goToLoginLink; // This FXML ID isn't strictly needed but good practice

    private final GuestService guestService = new GuestService();

    @FXML
    protected void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        if (guestService.getGuestByEmail(email) != null) {
            errorLabel.setText("This email is already registered.");
            return;
        }

        Guest newGuest = new Guest();
        newGuest.setName(name);
        newGuest.setEmail(email);
        newGuest.setPassword(password);

        guestService.saveGuest(newGuest);

        showAlertAndGoToLogin();
    }

    private void showAlertAndGoToLogin() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText(null);
        alert.setContentText("Your account has been created successfully! Please log in.");
        alert.showAndWait();
        
        handleGoToLogin();
    }

    @FXML
    protected void handleGoToLogin() {
        try {
            // Use any control in the scene to get the window
            Stage stage = (Stage) nameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginScreen.fxml"));
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("Hotel Reservation System Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}