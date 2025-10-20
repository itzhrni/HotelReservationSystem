package org.example.model;

import jakarta.persistence.*;
import javafx.beans.property.*;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_seq")
    @SequenceGenerator(name = "room_seq", sequenceName = "ROOM_ID_SEQ", allocationSize = 1)
    private int id;

    @Column(unique = true, nullable = false)
    private String roomNumber;

    private String type; // Single, Double, Suite, etc.

    private double price;

    @Transient
    private StringProperty availability = new SimpleStringProperty("Available"); // default

    // ===== JavaFX Properties =====
    public StringProperty roomNumberProperty() { return new SimpleStringProperty(roomNumber); }
    public StringProperty roomTypeProperty() { return new SimpleStringProperty(type); }
    public DoubleProperty priceProperty() { return new SimpleDoubleProperty(price); }
    public StringProperty availabilityProperty() { return availability; }

    // ===== Getters & Setters for JPA =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getAvailability() { return availability.get(); }
    public void setAvailability(String availability) { this.availability.set(availability); }

    // ===== Constructors =====
    public Room() {}

    public Room(String roomNumber, String type, double price, String availability) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.availability.set(availability);
    }
}
