package org.example.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Booking;
import org.example.model.Guest;
import org.example.service.BookingService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MyBookingsController {
    @FXML private TableView<Booking> bookingsTableView;
    @FXML private TableColumn<Booking, String> roomNumberCol;
    @FXML private TableColumn<Booking, String> roomTypeCol;
    @FXML private TableColumn<Booking, LocalDate> checkInCol;
    @FXML private TableColumn<Booking, LocalDate> checkOutCol;
    @FXML private TableColumn<Booking, Double> billCol;
    @FXML private TableColumn<Booking, String> statusCol;

    private Guest currentGuest;
    private final BookingService bookingService = new BookingService();

    @FXML
    public void initialize() {
        roomNumberCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoom().getRoomNumber()));
        roomTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoom().getType()));
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        billCol.setCellValueFactory(new PropertyValueFactory<>("billAmount"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
    }

    public void loadBookings(Guest guest) {
        this.currentGuest = guest;
        List<Booking> bookings = bookingService.getBookingsByGuest(guest);
        ObservableList<Booking> bookingData = FXCollections.observableArrayList(bookings);
        bookingsTableView.setItems(bookingData);
    }

    @FXML
    private void handleCancelBooking() {
        Booking selectedBooking = bookingsTableView.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a booking to cancel.");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Cancellation");
        confirmation.setHeaderText("Are you sure you want to cancel the booking for Room " + selectedBooking.getRoom().getRoomNumber() + "?");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            bookingService.deleteBooking(selectedBooking);
            loadBookings(currentGuest);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Booking cancelled.");
        }
    }

    // NEW: pay logic
    @FXML
    private void handlePayBooking() {
        Booking selectedBooking = bookingsTableView.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a booking to pay for.");
            return;
        }
        if ("PAID".equals(selectedBooking.getPaymentStatus())) {
            showAlert(Alert.AlertType.INFORMATION, "Already Paid", "Booking already marked as paid.");
            return;
        }
        selectedBooking.setPaymentStatus("PAID");
        bookingService.updateBooking(selectedBooking);
        loadBookings(currentGuest);
        showAlert(Alert.AlertType.INFORMATION, "Payment Complete", "Booking marked as paid!");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
