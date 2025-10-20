package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.util.HibernateUtil;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginScreen.fxml"));
        primaryStage.setTitle("Hotel Reservation System");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Cleanly close the Hibernate SessionFactory
        HibernateUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}