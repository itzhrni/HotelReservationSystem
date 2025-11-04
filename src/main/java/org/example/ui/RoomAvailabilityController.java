package org.example.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.Booking;
import org.example.model.Guest;
import org.example.model.Room;
import org.example.service.BookingService;
import org.example.service.RoomService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RoomAvailabilityController {

    @FXML private DatePicker checkInDatePicker;
    @FXML private DatePicker checkOutDatePicker;
    @FXML private TableView<Room> roomTableView;
    @FXML private TableColumn<Room, String> roomNumberCol;
    @FXML private TableColumn<Room, String> roomTypeCol;
    @FXML private TableColumn<Room, Double> priceCol;
    @FXML private TableColumn<Room, String> availabilityCol;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> roomTypeFilter;
    @FXML private ComboBox<String> sortFilter;
    @FXML private Label statusLabel;

    private Guest currentGuest;
    private final RoomService roomService = new RoomService();
    private final BookingService bookingService = new BookingService();

    public void setCurrentGuest(Guest guest) {
        this.currentGuest = guest;
    }

    @FXML
    public void initialize() {
        // Table columns
        roomNumberCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));

        // Availability coloring
        availabilityCol.setCellFactory(col -> new TableCell<Room, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equalsIgnoreCase("Available")) {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Default dates
        checkInDatePicker.setValue(LocalDate.now());
        checkOutDatePicker.setValue(LocalDate.now().plusDays(1));

        // Filters
        roomTypeFilter.setItems(FXCollections.observableArrayList("All", "Single", "Double", "Suite", "Deluxe"));
        sortFilter.setItems(FXCollections.observableArrayList("Default", "Price: Low to High", "Price: High to Low"));

        // Load initial rooms
        handleSearch();

        // Search/filter functionality
        searchField.textProperty().addListener((obs, oldVal, newVal) -> handleSearch());
        roomTypeFilter.valueProperty().addListener((obs, oldVal, newVal) -> handleSearch());
        sortFilter.valueProperty().addListener((obs, oldVal, newVal) -> handleSearch());
    }

    @FXML
    protected void handleSearch() {
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Dates", "Check-out date must be after check-in date.");
            return;
        }

        List<Room> availableRooms = roomService.findAvailableRooms(checkIn, checkOut);

        // Apply filters
        String searchText = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        String selectedType = roomTypeFilter.getValue();

        ObservableList<Room> filteredRooms = FXCollections.observableArrayList();
        for (Room room : availableRooms) {
            boolean matchesSearch = room.getRoomNumber().toLowerCase().contains(searchText)
                    || room.getType().toLowerCase().contains(searchText);
            boolean matchesType = selectedType == null || selectedType.equals("All")
                    || room.getType().equalsIgnoreCase(selectedType);
            if (matchesSearch && matchesType) filteredRooms.add(room);
        }

        // Apply sorting
        String sortOption = sortFilter.getValue();
        if ("Price: Low to High".equals(sortOption)) {
            filteredRooms.sort((r1, r2) -> Double.compare(r1.getPrice(), r2.getPrice()));
        } else if ("Price: High to Low".equals(sortOption)) {
            filteredRooms.sort((r1, r2) -> Double.compare(r2.getPrice(), r1.getPrice()));
        }

        roomTableView.setItems(filteredRooms);
        statusLabel.setText(filteredRooms.size() + " room(s) found");
    }

    @FXML
    protected void handleBooking() {
        Room selectedRoom = roomTableView.getSelectionModel().getSelectedItem();
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (selectedRoom == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a room to book.");
            return;
        }

        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Dates", "Please select valid check-in and check-out dates.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Booking");
        confirmation.setHeaderText("Book room " + selectedRoom.getRoomNumber() + "?");
        confirmation.setContentText("Guest: " + currentGuest.getName() + "\nCheck-in: " + checkIn + "\nCheck-out: " + checkOut);

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Booking newBooking = new Booking();
            newBooking.setGuest(currentGuest);
            newBooking.setRoom(selectedRoom);
            newBooking.setCheckInDate(checkIn);
            newBooking.setCheckOutDate(checkOut);

            // ===================
            // Bill and payment logic
            long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
            double billAmount = selectedRoom.getPrice() * nights;
            newBooking.setBillAmount(billAmount);
            newBooking.setPaymentStatus("DUE");
            // ===================

            bookingService.createBooking(newBooking);

            showAlert(Alert.AlertType.INFORMATION,
                    "Booking Success",
                    "Booking created!\nBill: ₹" + billAmount + "\nStatus: " + newBooking.getPaymentStatus());
            handleSearch(); // Refresh available rooms
        }
    }

    @FXML
    protected void showMyBookings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MyBookings.fxml"));
            Parent root = loader.load();

            MyBookingsController controller = loader.getController();
            controller.loadBookings(currentGuest);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("My Bookings");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginScreen.fxml"));
            Stage stage = (Stage) roomTableView.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("Hotel Reservation System Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
