package com.hospital.sanrafael.controller;

import com.hospital.sanrafael.model.Doctor;
import com.hospital.sanrafael.model.Schedule;
import com.hospital.sanrafael.model.User;
import com.hospital.sanrafael.service.DoctorService;
import com.hospital.sanrafael.view.ViewFactory;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class DoctorMenuController {
    private final ViewFactory viewFactory;
    private final DoctorService doctorService;
    private MainController mainController;
    private TextField firstNameField, lastNameField, emailField, phoneField;
    private TextField birthDateField, genderField, addressField;
    private TextField specialtyField, licenseField, areaField, experienceField;

    public DoctorMenuController(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
        this.doctorService = new DoctorService();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public Pane getView() {
        String mode = mainController != null ? mainController.getDoctorMenuMode() : "menu";
        switch (mode) {
            case "profile": return buildProfileView();
            case "schedule": return buildScheduleView();
            default: return buildMenuView();
        }
    }

    private Pane buildMenuView() {
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

        Label welcomeLabel = new Label("Panel del Doctor");
        welcomeLabel.setFont(Font.font("Arial", 16));
        welcomeLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-text-alignment: center;");
        welcomeLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox navBox = new VBox(15);
        navBox.setAlignment(Pos.CENTER);
        navBox.setPadding(new Insets(30, 0, 0, 0));

        Button modifyBtn = new Button("Modificar datos");
        modifyBtn.setPrefWidth(250);
        modifyBtn.setStyle("-fx-background-color: #2C3E8F; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 8; -fx-padding: 12 20;");
        modifyBtn.setOnMouseEntered(e -> modifyBtn.setStyle("-fx-background-color: #1a2a6a; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 8; -fx-padding: 12 20;"));
        modifyBtn.setOnMouseExited(e -> modifyBtn.setStyle("-fx-background-color: #2C3E8F; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 8; -fx-padding: 12 20;"));
        modifyBtn.setOnAction(e -> {
            if (mainController != null) {
                mainController.setDoctorMenuMode("profile");
                mainController.navigateTo("doctor-menu");
            }
        });

        Button enterBtn = new Button("Ingresar al sistema");
        enterBtn.setPrefWidth(250);
        enterBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 8; -fx-padding: 12 20;");
        enterBtn.setOnMouseEntered(e -> enterBtn.setStyle("-fx-background-color: #219a52; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 8; -fx-padding: 12 20;"));
        enterBtn.setOnMouseExited(e -> enterBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 8; -fx-padding: 12 20;"));
        enterBtn.setOnAction(e -> {
            if (mainController != null) {
                mainController.setDoctorMenuMode("schedule");
                mainController.navigateTo("doctor-menu");
            }
        });

        navBox.getChildren().addAll(modifyBtn, enterBtn);

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

    private Pane buildProfileView() {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #f0f4f8;");

        VBox leftPanel = createLeftPanel();
        leftPanel.setMinWidth(420);
        leftPanel.setMaxWidth(420);
        bp.setLeft(leftPanel);

        VBox panel = new VBox(20);
        panel.setPadding(new Insets(40, 60, 40, 60));
        panel.setStyle("-fx-background-color: #f0f4f8;");

        Label title = new Label("Mis Datos");
        title.setFont(Font.font("Arial Bold", 24));
        title.setStyle("-fx-text-fill: #2c3e50;");

        VBox formBox = new VBox(15);
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        firstNameField = new TextField();
        firstNameField.setPrefWidth(200);
        firstNameField.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ddd; -fx-font-size: 13px;");
        lastNameField = new TextField();
        lastNameField.setPrefWidth(200);
        lastNameField.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ddd; -fx-font-size: 13px;");
        emailField = new TextField();
        emailField.setPrefWidth(200);
        emailField.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ddd; -fx-font-size: 13px;");
        phoneField = new TextField();
        phoneField.setPrefWidth(200);
        phoneField.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ddd; -fx-font-size: 13px;");
        birthDateField = new TextField();
        birthDateField.setPrefWidth(200);
        birthDateField.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ddd; -fx-font-size: 13px;");
        genderField = new TextField();
        genderField.setPrefWidth(200);
        genderField.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ddd; -fx-font-size: 13px;");
        addressField = new TextField();
        addressField.setPrefWidth(200);
        addressField.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ddd; -fx-font-size: 13px;");
        specialtyField = new TextField();
        specialtyField.setPrefWidth(200);
        specialtyField.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ddd; -fx-font-size: 13px;");
        licenseField = new TextField();
        licenseField.setPrefWidth(200);
        licenseField.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ddd; -fx-font-size: 13px;");
        areaField = new TextField();
        areaField.setPrefWidth(200);
        areaField.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ddd; -fx-font-size: 13px;");
        experienceField = new TextField();
        experienceField.setPrefWidth(200);
        experienceField.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ddd; -fx-font-size: 13px;");

        int r = 0;
        grid.add(fieldLabel("Nombre:"), 0, r); grid.add(firstNameField, 1, r);
        grid.add(fieldLabel("Apellido:"), 2, r); grid.add(lastNameField, 3, r++);
        grid.add(fieldLabel("Email:"), 0, r); grid.add(emailField, 1, r);
        grid.add(fieldLabel("Tel\u00E9fono:"), 2, r); grid.add(phoneField, 3, r++);
        grid.add(fieldLabel("Fecha Nac.:"), 0, r); grid.add(birthDateField, 1, r);
        grid.add(fieldLabel("G\u00E9nero:"), 2, r); grid.add(genderField, 3, r++);
        grid.add(fieldLabel("Direcci\u00F3n:"), 0, r); grid.add(addressField, 1, r);
        grid.add(fieldLabel("Especialidad:"), 2, r); grid.add(specialtyField, 3, r++);
        grid.add(fieldLabel("N\u00B0 Colegiado:"), 0, r); grid.add(licenseField, 1, r);
        grid.add(fieldLabel("\u00C1rea:"), 2, r); grid.add(areaField, 3, r++);
        grid.add(fieldLabel("A\u00F1os Exp.:"), 0, r); grid.add(experienceField, 1, r);

        loadDoctorData();

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button saveBtn = new Button("Guardar cambios");
        saveBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 22; -fx-cursor: hand; -fx-font-size: 13px;");
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle("-fx-background-color: #219a52; -fx-text-fill: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 22; -fx-cursor: hand; -fx-font-size: 13px;"));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 22; -fx-cursor: hand; -fx-font-size: 13px;"));
        saveBtn.setOnAction(e -> saveProfile());

        Button backBtn = new Button("Volver");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E8F; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-color: #2C3E8F; -fx-border-radius: 8; -fx-padding: 8 18;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #2C3E8F; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-radius: 8; -fx-padding: 8 18;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E8F; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-color: #2C3E8F; -fx-border-radius: 8; -fx-padding: 8 18;"));
        backBtn.setOnAction(e -> {
            if (mainController != null) {
                mainController.setDoctorMenuMode("menu");
                mainController.navigateTo("doctor-menu");
            }
        });

        buttons.getChildren().addAll(saveBtn, backBtn);
        formBox.getChildren().addAll(grid, buttons);
        panel.getChildren().addAll(title, formBox);
        bp.setCenter(panel);
        return bp;
    }

    private Pane buildScheduleView() {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #f0f4f8;");

        VBox leftPanel = createLeftPanel();
        leftPanel.setMinWidth(420);
        leftPanel.setMaxWidth(420);
        bp.setLeft(leftPanel);

        VBox panel = new VBox(20);
        panel.setPadding(new Insets(40, 60, 40, 60));
        panel.setStyle("-fx-background-color: #f0f4f8;");

        Doctor doctor = findDoctorByUser();
        Label title = new Label("Mi Horario");
        title.setFont(Font.font("Arial Bold", 24));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Label docInfo = new Label();
        if (doctor != null) {
            docInfo.setText("Dr. " + doctor.getFullName() + "  |  " + doctor.getSpecialty() + "  |  " + doctor.getAssignedArea());
        } else {
            docInfo.setText("Doctor no encontrado");
        }
        docInfo.setFont(Font.font("Arial", 14));
        docInfo.setStyle("-fx-text-fill: #555;");

        VBox tableSection = new VBox(10);
        tableSection.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label sectionTitle = new Label("Horarios Asignados");
        sectionTitle.setFont(Font.font("Arial Bold", 16));
        sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

        TableView<Schedule> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefHeight(300);

        TableColumn<Schedule, String> dayCol = new TableColumn<>("D\u00EDa");
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        TableColumn<Schedule, String> startCol = new TableColumn<>("Inicio");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        TableColumn<Schedule, String> endCol = new TableColumn<>("Fin");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        TableColumn<Schedule, String> activityCol = new TableColumn<>("Actividad");
        activityCol.setCellValueFactory(new PropertyValueFactory<>("activity"));
        TableColumn<Schedule, String> classroomCol = new TableColumn<>("Aula");
        classroomCol.setCellValueFactory(new PropertyValueFactory<>("classroom"));

        tableView.getColumns().addAll(dayCol, startCol, endCol, activityCol, classroomCol);

        if (doctor != null) {
            tableView.setItems(FXCollections.observableArrayList(doctor.getCareSchedule()));
        }

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button backBtn = new Button("Volver");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E8F; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-color: #2C3E8F; -fx-border-radius: 8; -fx-padding: 8 18;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #2C3E8F; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-radius: 8; -fx-padding: 8 18;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E8F; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-color: #2C3E8F; -fx-border-radius: 8; -fx-padding: 8 18;"));
        backBtn.setOnAction(e -> {
            if (mainController != null) {
                mainController.setDoctorMenuMode("menu");
                mainController.navigateTo("doctor-menu");
            }
        });

        buttons.getChildren().add(backBtn);
        tableSection.getChildren().addAll(sectionTitle, tableView, buttons);
        panel.getChildren().addAll(title, docInfo, tableSection);
        bp.setCenter(panel);
        return bp;
    }

    private Doctor findDoctorByUser() {
        if (mainController == null) return null;
        User user = mainController.getCurrentUser();
        if (user == null) return null;

        String fullName = user.getFullName().toLowerCase();
        Doctor matchedDoctor = null;
        for (Doctor d : doctorService.getAllDoctors()) {
            String docName = (d.getFirstName() + " " + d.getLastName()).toLowerCase();
            if (docName.equals(fullName) || docName.contains(fullName) || fullName.contains(docName)) {
                matchedDoctor = d;
                break;
            }
        }
        return matchedDoctor;
    }

    private void loadDoctorData() {
        Doctor matchedDoctor = findDoctorByUser();
        if (matchedDoctor != null) {
            firstNameField.setText(matchedDoctor.getFirstName());
            lastNameField.setText(matchedDoctor.getLastName());
            emailField.setText(matchedDoctor.getEmail());
            phoneField.setText(matchedDoctor.getPhone());
            birthDateField.setText(matchedDoctor.getBirthDate());
            genderField.setText(matchedDoctor.getGender());
            addressField.setText(matchedDoctor.getAddress());
            specialtyField.setText(matchedDoctor.getSpecialty());
            licenseField.setText(matchedDoctor.getLicenseNumber());
            areaField.setText(matchedDoctor.getAssignedArea());
            experienceField.setText(String.valueOf(matchedDoctor.getYearsExperience()));
        }
    }

    private void saveProfile() {
        try {
            Doctor matchedDoctor = findDoctorByUser();
            if (matchedDoctor == null) {
                showAlert("Error", "Doctor no encontrado en la base de datos");
                return;
            }

            matchedDoctor.setFirstName(firstNameField.getText());
            matchedDoctor.setLastName(lastNameField.getText());
            matchedDoctor.setEmail(emailField.getText());
            matchedDoctor.setPhone(phoneField.getText());
            matchedDoctor.setBirthDate(birthDateField.getText());
            matchedDoctor.setGender(genderField.getText());
            matchedDoctor.setAddress(addressField.getText());
            matchedDoctor.setSpecialty(specialtyField.getText());
            matchedDoctor.setLicenseNumber(licenseField.getText());
            matchedDoctor.setAssignedArea(areaField.getText());
            matchedDoctor.setYearsExperience(parseInt(experienceField.getText(), 0));

            doctorService.updateDoctor(matchedDoctor);
            showAlert("\u00C9xito", "Datos actualizados correctamente");
        } catch (Exception ex) {
            showAlert("Error", "Error: " + ex.getMessage());
        }
    }

    private Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", 12));
        l.setStyle("-fx-text-fill: #555;");
        return l;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int parseInt(String val, int def) {
        if (val == null || val.trim().isEmpty()) return def;
        try { return Integer.parseInt(val.trim()); } catch (NumberFormatException e) { return def; }
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
