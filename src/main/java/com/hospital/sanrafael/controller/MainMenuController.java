package com.hospital.sanrafael.controller;

import com.hospital.sanrafael.service.InitialDataService;
import com.hospital.sanrafael.view.ViewFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class MainMenuController {
    private final ViewFactory viewFactory;
    private MainController mainController;
    private static boolean initialDataLoaded = false;

    public MainMenuController(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
        if (!initialDataLoaded) {
            loadInitialData();
            initialDataLoaded = true;
        }
    }

    private void loadInitialData() {
        try {
            InitialDataService dataService = new InitialDataService();
            dataService.loadExampleData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public Pane getView() {
        HBox root = new HBox();
        root.setPadding(new Insets(0));

        VBox leftPanel = createLeftPanel();
        ImageView hospitalImage = createHospitalImage();

        leftPanel.setMinWidth(420);
        leftPanel.setMaxWidth(420);

        root.getChildren().addAll(leftPanel, hospitalImage);
        return root;
    }

    private VBox createLeftPanel() {
        VBox panel = new VBox(20);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(50, 30, 40, 30));
        panel.setStyle("-fx-background-color: white;");

        VBox logoBox = new VBox(5);
        logoBox.setAlignment(Pos.CENTER);

        try {
            java.io.InputStream logoStream = getClass().getResourceAsStream("/imagenes/logo.png");
            if (logoStream == null) throw new Exception("Logo not found");
            Image logoImg = new Image(logoStream);
            ImageView logoView = new ImageView(logoImg);
            logoView.setFitWidth(300);
            logoView.setPreserveRatio(true);
            logoBox.getChildren().add(logoView);
        } catch (Exception e) {
            Label title = new Label("Hospital Universitario\nSan Rafael de Tunja");
            title.setFont(Font.font("Arial Bold", 20));
            title.setStyle("-fx-text-fill: #1a5f7a; -fx-text-alignment: center;");
            logoBox.getChildren().add(title);
        }

        Label welcomeLabel = new Label("Bienvenido al Sistema de Gesti\u00F3n");
        welcomeLabel.setFont(Font.font("Arial", 16));
        welcomeLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-text-alignment: center;");
        welcomeLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox navBox = new VBox(10);
        navBox.setAlignment(Pos.CENTER);
        navBox.setPadding(new Insets(20, 0, 0, 0));

        Button enterBtn = new Button("Ingresar al Sistema");
        enterBtn.setPrefWidth(250);
        enterBtn.setStyle("-fx-background-color: #1a5f7a; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 8; -fx-padding: 12 20;");
        enterBtn.setOnMouseEntered(e -> enterBtn.setStyle("-fx-background-color: #154a60; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 8; -fx-padding: 12 20;"));
        enterBtn.setOnMouseExited(e -> enterBtn.setStyle("-fx-background-color: #1a5f7a; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 8; -fx-padding: 12 20;"));
        enterBtn.setOnAction(e -> { if (mainController != null) mainController.navigateTo("students"); });
        navBox.getChildren().add(enterBtn);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox logoutBox = new HBox();
        logoutBox.setAlignment(Pos.CENTER);
        logoutBox.setPadding(new Insets(20, 0, 0, 0));

        Button logoutBtn = new Button("Cerrar Sesi\u00F3n");
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-padding: 8 20;");
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-radius: 8; -fx-padding: 8 20;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-padding: 8 20;"));
        logoutBtn.setOnAction(e -> {
            if (mainController != null) mainController.navigateTo("login");
        });
        logoutBox.getChildren().add(logoutBtn);

        panel.getChildren().addAll(logoBox, welcomeLabel, navBox, spacer, logoutBox);
        return panel;
    }

    private ImageView createHospitalImage() {
        ImageView hospitalView = new ImageView();
        try {
            Image hospitalImage = new Image(getClass().getResourceAsStream("/imagenes/hospital.jpeg"));
            if (hospitalImage == null || hospitalImage.isError() || hospitalImage.getWidth() == 0) {
                throw new Exception("Resource image error");
            }
            hospitalView = new ImageView(hospitalImage);
        } catch (Exception e) {
            try {
                Image hospitalImage = new Image(new java.io.FileInputStream("src/main/resources/imagenes/hospital.jpeg"));
                hospitalView = new ImageView(hospitalImage);
            } catch (Exception ex) {
                try {
                    Image hospitalImage = new Image(new java.io.FileInputStream("imagenes/WhatsApp Image 2026-05-23 at 7.45.51 AM.jpeg"));
                    hospitalView = new ImageView(hospitalImage);
                } catch (Exception ex2) {
                    System.err.println("Hospital image not found");
                    return new ImageView();
                }
            }
        }

        hospitalView.setPreserveRatio(true);
        hospitalView.setSmooth(true);
        return hospitalView;
    }
}
