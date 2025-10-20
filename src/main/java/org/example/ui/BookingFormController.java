package org.example.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.model.Booking;
import org.example.model.Guest;
import org.example.model.Room;
import org.example.service.BookingService;

import java.time.LocalDate;

public class BookingFormController {

    @FXML private Label guestNameLabel;
    @FXML private Label roomNumberLabel;
    @FXML private Label checkInLabel;
    @FXML private Label checkOutLabel;

    private Guest guest;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private BookingService bookingService = new BookingService();
    private boolean confirmed = false;

    public void setBookingDetails(Guest guest, Room room, LocalDate checkIn, LocalDate checkOut) {
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;

        guestNameLabel.setText(guest.getName());
        roomNumberLabel.setText(room.getRoomNumber());
        checkInLabel.setText(checkIn.toString());
        checkOutLabel.setText(checkOut.toString());
    }

    @FXML
    private void handleConfirmBooking() {
        Booking newBooking = new Booking();
        newBooking.setGuest(guest);
        newBooking.setRoom(room);
        newBooking.setCheckInDate(checkInDate);
        newBooking.setCheckOutDate(checkOutDate);

        bookingService.createBooking(newBooking);

        confirmed = true;
        closeWindow();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) guestNameLabel.getScene().getWindow();
        stage.close();
    }
}