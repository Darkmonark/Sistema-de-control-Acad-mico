package com.hospital.sanrafael.controller;

import com.hospital.sanrafael.model.User;
import com.hospital.sanrafael.service.AuthService;
import com.hospital.sanrafael.view.ViewFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.collections.FXCollections;

public class LoginController {
    private final ViewFactory viewFactory;
    private final AuthService authService;
    private MainController mainController;
    private TextField usernameField;
    private PasswordField passwordField;
    private ComboBox<String> roleCombo;

    public LoginController(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
        this.authService = new AuthService();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public Pane getView() {
        HBox root = new HBox();

        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();

        leftPanel.setMinWidth(700);
        leftPanel.setMaxWidth(700);

        root.getChildren().addAll(leftPanel, rightPanel);
        return root;
    }

    private VBox createLeftPanel() {
        VBox panel = new VBox();
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a3a6a, #2C3E8F);");
        panel.setPadding(new Insets(60, 40, 60, 40));

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(500);

        ImageView logoView = null;
        try {
            java.io.InputStream logoStream = getClass().getResourceAsStream("/imagenes/logo.png");
            if (logoStream != null) {
                Image logoImg = new Image(logoStream);
                logoView = new ImageView(logoImg);
                logoView.setFitWidth(200);
                logoView.setPreserveRatio(true);
            }
        } catch (Exception e) { }

        Label title = new Label("Hospital Universitario\nSan Rafael de Tunja");
        title.setFont(Font.font("Arial Bold", 32));
        title.setStyle("-fx-text-fill: white; -fx-text-alignment: center;");
        title.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label subtitle = new Label("Sistema de Gesti\u00F3n Hospitalaria\nControl de acceso seguro");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-text-alignment: center;");
        subtitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

VBox features = new VBox(12);
features.setAlignment(Pos.CENTER_LEFT);
features.setPadding(new Insets(30, 0, 0, 0));

features.getChildren().addAll(
featureRow("", "Gesti\u00F3n de Doctores y pacientes"),
featureRow("", "Control de estudiantes y horarios"),
featureRow("", "Registro de ingresos hospitalarios"),
featureRow("", "Acceso seguro por roles")
);

Label credsLabel = new Label("USUARIO: admin / doctor1 / estudiante1\nCLAVE: admin123 / doctor123 / estudiante123");
credsLabel.setFont(Font.font("Arial Bold", 11));
credsLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.7); -fx-text-alignment: center;");
credsLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
features.getChildren().add(credsLabel);

        if (logoView != null) {
            content.getChildren().add(logoView);
        }
        content.getChildren().addAll(title, subtitle, features);
        panel.getChildren().add(content);
        return panel;
    }

    private HBox featureRow(String icon, String text) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label textLbl = new Label(text);
        textLbl.setFont(Font.font("Arial", 14));
        textLbl.setStyle("-fx-text-fill: rgba(255,255,255,0.9);");
        row.getChildren().add(textLbl);
        return row;
    }

    private VBox createRightPanel() {
        VBox panel = new VBox();
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("-fx-background-color: white;");
        panel.setPadding(new Insets(50, 50, 50, 50));
        HBox.setHgrow(panel, Priority.ALWAYS);

        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(380);

        Label welcome = new Label("Bienvenido");
        welcome.setFont(Font.font("Arial Bold", 28));
        welcome.setStyle("-fx-text-fill: #2c3e50;");

        Label instruct = new Label("Inicia sesi\u00F3n para acceder al sistema");
        instruct.setFont(Font.font("Arial", 14));
        instruct.setStyle("-fx-text-fill: #95a5a6;");

        Label roleLabel = new Label("Seleccionar Rol");
        roleLabel.setFont(Font.font("Arial Bold", 13));
        roleLabel.setStyle("-fx-text-fill: #555;");

        roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList("Seleccione un rol", "Administrador", "Doctor", "Estudiante"));
        roleCombo.setValue("Seleccione un rol");
        roleCombo.setPrefWidth(380);
        roleCombo.setPrefHeight(45);
        roleCombo.setStyle("-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 5; -fx-border-color: #ddd; -fx-font-size: 14px;");

        Label userLabel = new Label("Usuario");
        userLabel.setFont(Font.font("Arial Bold", 13));
        userLabel.setStyle("-fx-text-fill: #555;");

        usernameField = new TextField();
        usernameField.setPromptText("Ingrese su usuario");
        usernameField.setPrefWidth(380);
        usernameField.setPrefHeight(45);
        usernameField.setStyle("-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-border-color: #ddd; -fx-font-size: 14px;");

        Label passLabel = new Label("Contrase\u00F1a");
        passLabel.setFont(Font.font("Arial Bold", 13));
        passLabel.setStyle("-fx-text-fill: #555;");

        passwordField = new PasswordField();
        passwordField.setPromptText("Ingrese su contrase\u00F1a");
        passwordField.setPrefWidth(380);
        passwordField.setPrefHeight(45);
        passwordField.setStyle("-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-border-color: #ddd; -fx-font-size: 14px;");
        passwordField.setOnAction(e -> doLogin());

        Button loginBtn = new Button("Iniciar Sesi\u00F3n");
        loginBtn.setPrefWidth(380);
        loginBtn.setPrefHeight(48);
        loginBtn.setStyle(
            "-fx-background-color: #2C3E8F;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: #1a2a6a; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: #2C3E8F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        loginBtn.setOnAction(e -> doLogin());

        HBox registerRow = new HBox(5);
        registerRow.setAlignment(Pos.CENTER);
        Label noAccount = new Label("\u00BFNo tienes cuenta?");
        noAccount.setFont(Font.font("Arial", 13));
        noAccount.setStyle("-fx-text-fill: #7f8c8d;");
        Label registerLink = new Label("Reg\u00EDstrate");
        registerLink.setFont(Font.font("Arial Bold", 13));
        registerLink.setStyle("-fx-text-fill: #2C3E8F; -fx-cursor: hand; -fx-underline: true;");
        registerLink.setOnMouseClicked(e -> {
            if (mainController != null) mainController.navigateTo("register");
        });
        registerRow.getChildren().addAll(noAccount, registerLink);

        card.getChildren().addAll(welcome, instruct, roleLabel, roleCombo, userLabel, usernameField, passLabel, passwordField, loginBtn, registerRow);
        panel.getChildren().add(card);
        return panel;
    }

    private void doLogin() {
        String role = roleCombo.getValue();
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (role.equals("Seleccione un rol")) {
            showAlert("Error", "Por favor seleccione un rol");
            return;
        }
        if (user.isEmpty() || pass.isEmpty()) {
            showAlert("Error", "Por favor ingrese usuario y contrase\u00F1a");
            return;
        }

        User usuario = authService.login(user, pass);
        if (usuario != null) {
            if (!usuario.getRole().equals(role)) {
                showAlert("Error", "El rol seleccionado no coincide con el usuario");
                return;
            }
if (mainController != null) {
mainController.setCurrentUser(usuario);
if (usuario.getRole().equals("Doctor")) {
mainController.navigateTo("doctor-dashboard");
} else if (usuario.getRole().equals("Estudiante")) {
mainController.navigateTo("student-dashboard");
} else {
mainController.navigateTo("main");
}
}
        } else {
            showAlert("Error", "Usuario o contrase\u00F1a incorrectos");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
