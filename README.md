Hotel Room Reservation System

A fully functional desktop application for managing hotel room reservations. This system provides separate interfaces for guests to book rooms and for administrators to manage the hotel's inventory. It is built with a robust MVC (Model-View-Controller) architecture to ensure a clear separation of concerns, maintainability, and scalability.

Features

Guest Portal:

Secure user registration and login.

Search for available rooms based on check-in and check-out dates.

View a list of available rooms with details like type and price.

Book a room for the selected dates.

View a list of personal past and upcoming bookings.

Ability to cancel an upcoming booking.

A dedicated logout button to securely end the session.

Admin Dashboard:

Secure admin login with separate credentials.

View a complete list of all rooms in the hotel.

Add new rooms with a room number, type, and price.

Delete existing rooms from the system.

View a comprehensive list of all bookings made by all guests, including their names and booking dates.

A dedicated logout button to return to the login screen.

Dynamic Availability: Room availability is calculated in real-time, ensuring no double bookings can occur. A room is automatically considered available after its last booking's check-out date has passed relative to a new search.

Technologies Used

Language: Java 17

Framework / UI: JavaFX

Database: Oracle Database 11g

ORM (Object-Relational Mapping): Hibernate 6

Build Tool: Apache Maven

Project Structure and Logic

The project follows the MVC design pattern to keep the application organized and easy to maintain.

1. model Package (src/main/java/org/example/model/)

Contains the entity classes that map to the database tables using Hibernate annotations.

Guest.java: Represents a guest user. Stores personal information and login credentials. It has a one-to-many relationship with Booking.

Room.java: Represents a single hotel room. Stores its unique number, type, and price per night.

Booking.java: Represents a reservation. It links a Guest to a Room for a specific period, defined by checkInDate and checkOutDate.

2. service Package (src/main/java/org/example/service/)

Contains the business logic of the application. The services act as a bridge between the UI controllers and the database, ensuring that the controllers remain lean.

GuestService.java: Handles all guest-related operations, such as saving a new guest upon registration and retrieving a guest by email for login authentication.

RoomService.java: Manages all room-related logic. Its key method, findAvailableRooms, executes a query to find rooms that have no overlapping bookings for a given date range. It also handles CRUD (Create, Read, Update, Delete) operations for the admin panel.

BookingService.java: Manages all booking operations, including creating a new booking, canceling a booking, and retrieving bookings for a specific guest or all bookings for the admin.

3. ui Package (src/main/java/org/example/ui/)

Contains the JavaFX controller classes. Each controller is linked to an FXML file and is responsible for handling user interactions and updating the view.

LoginController.java: Manages the login screen. It authenticates both regular guests and the special admin user, redirecting them to the appropriate dashboard. It also provides a link to the registration screen.

RegisterController.java: Manages the new user registration screen. It validates user input and uses the GuestService to save the new account to the database.

RoomAvailabilityController.java: The main screen for guests. It allows users to select dates, search for rooms, and initiate the booking process. It also provides navigation to view personal bookings and to log out.

MyBookingsController.java: Displays a list of bookings for the currently logged-in guest and allows them to cancel a selected booking.

AdminPanelController.java: The main screen for the administrator. It uses a TabPane to separate room management (add/delete rooms) from the global booking overview, which includes guest names and check-in/check-out dates.

4. util Package (src/main/java/org/example/util/)

Contains helper classes used across the application.

HibernateUtil.java: A standard Hibernate utility class responsible for creating and managing the global SessionFactory. This ensures that the application connects to the database efficiently.

5. resources Directory (src/main/resources/)

Contains all non-Java files, including the UI layout, styling, and configuration.

fxml/: Contains all the FXML files that define the structure and layout of the user interface screens (LoginScreen, RegisterScreen, RoomAvailability, MyBookings, and AdminPanel).

css/styles.css: The stylesheet used to provide a consistent and modern look and feel across all application windows.

hibernate.cfg.xml: The core configuration file for Hibernate. It contains the database connection details (URL, username, password) and the database dialect.

How to Set Up and Run

Prerequisites:

Java JDK 17

Apache Maven

Oracle Database 11g

Database Configuration:

Ensure your Oracle services (OracleServiceXE and OracleXETNSListener) are running.

Update the database connection details (username and password) in src/main/resources/hibernate.cfg.xml.

Build the Project:

Open a terminal in the project's root directory and run mvn clean install.

Run the Application:

Execute the command mvn javafx:run.

Admin Login: admin@hotel.com
 / adminpass

Guest Login: Register a new user or use a pre-inserted guest account.
