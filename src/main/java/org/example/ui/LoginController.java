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

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private final GuestService guestService = new GuestService();

    @FXML
    protected void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Email and password cannot be empty.");
            return;
        }

        if ("admin@hotel.com".equals(email) && "adminpass".equals(password)) {
            loadView("/fxml/AdminPanel.fxml", "Admin Panel", 800, 600, null);
            return;
        }

        Guest guest = guestService.getGuestByEmail(email);

        if (guest != null && guest.getPassword().equals(password)) {
            errorLabel.setText("");
            loadRoomAvailabilityView(guest);
        } else {
            errorLabel.setText("Invalid email or password.");
        }
    }

    private void loadView(String fxmlPath, String title, int width, int height, Guest guest) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (guest != null && loader.getController() instanceof RoomAvailabilityController) {
                RoomAvailabilityController controller = loader.getController();
                controller.setCurrentGuest(guest);
            }

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
            stage.setTitle(title);

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load the view: " + title);
        }
    }

    private void loadRoomAvailabilityView(Guest guest) {
        loadView("/fxml/RoomAvailability.fxml", "Room Availability", 800, 600, guest);
    }

    @FXML
    protected void handleGoToRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/RegisterScreen.fxml"));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle("Create New Account");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
