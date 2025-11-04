package org.example.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.model.Booking;
import org.example.model.Room;
import org.example.service.BookingService;
import org.example.service.RoomService;

import java.io.IOException;
import java.time.LocalDate;

public class AdminPanelController {
    @FXML private TableView<Room> roomManagementTable;
    @FXML private TableColumn<Room, String> roomNumberCol;
    @FXML private TableColumn<Room, String> roomTypeCol;
    @FXML private TableColumn<Room, Double> roomPriceCol;
    @FXML private TextField roomNumberField;
    @FXML private TextField roomTypeField;
    @FXML private TextField roomPriceField;

    @FXML private TableView<Booking> allBookingsTable;
    @FXML private TableColumn<Booking, Integer> bookingIdCol;
    @FXML private TableColumn<Booking, String> guestNameCol;
    @FXML private TableColumn<Booking, String> bookedRoomCol;
    @FXML private TableColumn<Booking, LocalDate> bookingCheckInCol;
    @FXML private TableColumn<Booking, LocalDate> bookingCheckOutCol;
    @FXML private TableColumn<Booking, Double> billCol;
    @FXML private TableColumn<Booking, String> statusCol;

    private final RoomService roomService = new RoomService();
    private final BookingService bookingService = new BookingService();

    @FXML
    public void initialize() {
        roomNumberCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        roomPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        bookingIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        guestNameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getGuest().getName()));
        bookedRoomCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getRoom().getRoomNumber()));
        bookingCheckInCol.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        bookingCheckOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        billCol.setCellValueFactory(new PropertyValueFactory<>("billAmount"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        refreshAllViews();
    }

    private void refreshAllViews() {
        roomManagementTable.setItems(FXCollections.observableArrayList(roomService.getAllRooms()));
        allBookingsTable.setItems(FXCollections.observableArrayList(bookingService.getAllBookings()));
    }

    @FXML
    private void handleAddRoom() {
        try {
            Room newRoom = new Room();
            newRoom.setRoomNumber(roomNumberField.getText());
            newRoom.setType(roomTypeField.getText());
            newRoom.setPrice(Double.parseDouble(roomPriceField.getText()));
            roomService.saveRoom(newRoom);
            clearRoomFields();
            refreshAllViews();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price must be a valid number.");
        }
    }

    @FXML
    private void handleDeleteRoom() {
        Room selectedRoom = roomManagementTable.getSelectionModel().getSelectedItem();
        if (selectedRoom != null) {
            roomService.deleteRoom(selectedRoom);
            refreshAllViews();
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a room to delete.");
        }
    }

    private void clearRoomFields() {
        roomNumberField.clear();
        roomTypeField.clear();
        roomPriceField.clear();
    }

    @FXML
    protected void handleLogout() {
        try {
            Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/LoginScreen.fxml"));
            Stage stage = (Stage) roomManagementTable.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("Hotel Reservation System Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }

    @FXML
    private void handleMarkAsPaid() {
        Booking selectedBooking = allBookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a booking to mark as paid.");
            return;
        }
        if ("PAID".equals(selectedBooking.getPaymentStatus())) {
            showAlert(Alert.AlertType.INFORMATION, "Already Paid", "Booking already marked as paid.");
            return;
        }
        selectedBooking.setPaymentStatus("PAID");
        bookingService.updateBooking(selectedBooking);
        refreshAllViews();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Selected booking marked as paid.");
    }
}
