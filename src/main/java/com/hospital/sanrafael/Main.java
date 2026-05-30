package com.hospital.sanrafael;

import com.hospital.sanrafael.controller.MainController;
import com.hospital.sanrafael.database.DatabaseConnection;
import com.hospital.sanrafael.database.DatabaseInitializer;
import com.hospital.sanrafael.view.FXViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseInitializer.ensureTablesExist();

            if (DatabaseConnection.testConnection()) {
                System.out.println("Connection to PostgreSQL successful");
            } else {
                System.out.println("Could not connect to PostgreSQL");
                System.out.println("The application will use local storage (.dat files)");
            }

            MainController mainController = new MainController(new FXViewFactory());
            mainController.initialize(primaryStage);
            primaryStage.setTitle("Hospital San Rafael - Management System");
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();

            System.out.println("\nStarting in offline mode (without database)");
            try {
                MainController mainController = new MainController(new FXViewFactory());
                mainController.initialize(primaryStage);
                primaryStage.setTitle("Hospital San Rafael - Offline Mode");
                primaryStage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
