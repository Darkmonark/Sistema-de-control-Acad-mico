package com.hospital.sanrafael.controller;

import com.hospital.sanrafael.model.Doctor;
import com.hospital.sanrafael.model.Student;
import com.hospital.sanrafael.service.AuthService;
import com.hospital.sanrafael.service.DoctorService;
import com.hospital.sanrafael.service.StudentService;
import com.hospital.sanrafael.view.ViewFactory;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class RegisterController {
    private final ViewFactory viewFactory;
    private final AuthService authService;
    private final StudentService studentService;
    private final DoctorService doctorService;
    private MainController mainController;
    private TextField usernameField, emailField, fullNameField , lastnameField;
    private PasswordField passwordField, confirmField;
    private ComboBox<String> roleCombo;
    private VBox extraFieldsContainer;
    private TextField phoneField, birthDateField, genderField, addressField;
    private TextField specialtyField, licenseNumberField;
    private ComboBox<Integer> semesterCombo;

    public RegisterController(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
        this.authService = new AuthService();
        this.studentService = new StudentService();
        this.doctorService = new DoctorService();
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
        panel.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a5f3a, #27AE60);");
        panel.setPadding(new Insets(60, 40, 60, 40));

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(500);

        try {
            java.io.InputStream logoStream = getClass().getResourceAsStream("/imagenes/logo.png");
            if (logoStream != null) {
                Image logoImg = new Image(logoStream);
                ImageView logoView = new ImageView(logoImg);
                logoView.setFitWidth(250);
                logoView.setPreserveRatio(true);
                content.getChildren().add(logoView);
            }
        } catch (Exception e) {
        }

        Label title = new Label("Crear tu Cuenta");
        title.setFont(Font.font("Arial Bold", 32));
        title.setStyle("-fx-text-fill: white; -fx-text-alignment: center;");
        title.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label subtitle = new Label("Reg\u00EDstrate seg\u00FAn tu rol\npara acceder al sistema");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-text-alignment: center;");
        subtitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox features = new VBox(12);
        features.setAlignment(Pos.CENTER_LEFT);
        features.setPadding(new Insets(30, 0, 0, 0));

        features.getChildren().addAll(
            featureRow("", "Doctores: gestiona pacientes y horarios"),
            featureRow("", "Estudiantes: acceso a materias y notas"),
            featureRow("", "Administradores: control total del sistema")
        );

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
        panel.setPadding(new Insets(40, 50, 40, 50));
        HBox.setHgrow(panel, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(380);

        Label title = new Label("Registro");
        title.setFont(Font.font("Arial Bold", 28));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Label instruct = new Label("Completa todos los campos para registrarte");
        instruct.setFont(Font.font("Arial", 14));
        instruct.setStyle("-fx-text-fill: #95a5a6;");

        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER_LEFT);

        form.getChildren().addAll(
            fieldLabel("Nombres"),
            fullNameField = createField("Ej: Juan Felipe"),

            fieldLabel("Apellidos"),
            lastnameField = createField("Ej: Gonzalo"),

            fieldLabel("Correo electr\u00F3nico"),
            emailField = createField("Ej: correo@ejemplo.com"),

            fieldLabel("Nombre de usuario"),
            usernameField = createField("Ej: juanperez"),

            fieldLabel("Rol"),
            roleCombo = createRoleCombo(),

            fieldLabel("Contrase\u00F1a"),
            passwordField = createPasswordField("M\u00EDnimo 4 caracteres"),

            fieldLabel("Confirmar contrase\u00F1a"),
            confirmField = createPasswordField("Repite la contrase\u00F1a")
        );

        extraFieldsContainer = new VBox(10);
        extraFieldsContainer.setVisible(false);
        extraFieldsContainer.setManaged(false);
        form.getChildren().add(extraFieldsContainer);

        roleCombo.setOnAction(e -> updateExtraFields());

        Button registerBtn = new Button("Crear Cuenta");
        registerBtn.setPrefWidth(380);
        registerBtn.setPrefHeight(48);
        registerBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        registerBtn.setOnMouseEntered(e -> registerBtn.setStyle("-fx-background-color: #219a52; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        registerBtn.setOnMouseExited(e -> registerBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        registerBtn.setOnAction(e -> doRegister());

        HBox loginRow = new HBox(5);
        loginRow.setAlignment(Pos.CENTER);
        Label hasAccount = new Label("\u00BFYa tienes cuenta?");
        hasAccount.setFont(Font.font("Arial", 13));
        hasAccount.setStyle("-fx-text-fill: #7f8c8d;");
        Label loginLink = new Label("Inicia sesi\u00F3n");
        loginLink.setFont(Font.font("Arial Bold", 13));
        loginLink.setStyle("-fx-text-fill: #2C3E8F; -fx-cursor: hand; -fx-underline: true;");
        loginLink.setOnMouseClicked(e -> {
            if (mainController != null) mainController.navigateTo("login");
        });
        loginRow.getChildren().addAll(hasAccount, loginLink);

        card.getChildren().addAll(title, instruct, form, registerBtn, loginRow);
        scroll.setContent(card);
        panel.getChildren().add(scroll);
        return panel;
    }

    private void updateExtraFields() {
        String role = roleCombo.getValue();
        extraFieldsContainer.getChildren().clear();
        extraFieldsContainer.setVisible(false);
        extraFieldsContainer.setManaged(false);

        if ("Doctor".equals(role)) {
            addDoctorFields();
        } else if ("Estudiante".equals(role)) {
            addStudentFields();
        }
    }

    private void addDoctorFields() {
        extraFieldsContainer.getChildren().add(new Separator());

        extraFieldsContainer.getChildren().add(fieldLabel("Tel\u00E9fono"));
        phoneField = createField("Ej: 555-0101");
        extraFieldsContainer.getChildren().add(phoneField);

        extraFieldsContainer.getChildren().add(fieldLabel("Fecha de Nacimiento"));
        birthDateField = createField("Ej: 1985-03-15");
        extraFieldsContainer.getChildren().add(birthDateField);

        extraFieldsContainer.getChildren().add(fieldLabel("G\u00E9nero"));
        genderField = createField("Ej: M o F");
        extraFieldsContainer.getChildren().add(genderField);

        extraFieldsContainer.getChildren().add(fieldLabel("Direcci\u00F3n"));
        addressField = createField("Ej: Av. Principal 123");
        extraFieldsContainer.getChildren().add(addressField);

        extraFieldsContainer.getChildren().add(fieldLabel("Especialidad"));
        specialtyField = createField("Ej: Medicina Interna");
        extraFieldsContainer.getChildren().add(specialtyField);

        extraFieldsContainer.getChildren().add(fieldLabel("N\u00B0 Colegiado"));
        licenseNumberField = createField("Ej: COL-12345");
        extraFieldsContainer.getChildren().add(licenseNumberField);

        extraFieldsContainer.setVisible(true);
        extraFieldsContainer.setManaged(true);
    }

    private void addStudentFields() {
        extraFieldsContainer.getChildren().add(new Separator());

        extraFieldsContainer.getChildren().add(fieldLabel("Tel\u00E9fono"));
        phoneField = createField("Ej: 555-1001");
        extraFieldsContainer.getChildren().add(phoneField);

        extraFieldsContainer.getChildren().add(fieldLabel("Fecha de Nacimiento"));
        birthDateField = createField("Ej: 2000-01-15");
        extraFieldsContainer.getChildren().add(birthDateField);

        extraFieldsContainer.getChildren().add(fieldLabel("G\u00E9nero"));
        genderField = createField("Ej: M o F");
        extraFieldsContainer.getChildren().add(genderField);

        extraFieldsContainer.getChildren().add(fieldLabel("Direcci\u00F3n"));
        addressField = createField("Ej: Calle 10 #20-30");
        extraFieldsContainer.getChildren().add(addressField);

        extraFieldsContainer.getChildren().add(fieldLabel("Semestre"));
        semesterCombo = new ComboBox<>();
        semesterCombo.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        semesterCombo.setPrefWidth(380);
        semesterCombo.setPrefHeight(42);
        semesterCombo.setStyle("-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 5; -fx-border-color: #ddd; -fx-font-size: 14px;");
        extraFieldsContainer.getChildren().add(semesterCombo);

        extraFieldsContainer.setVisible(true);
        extraFieldsContainer.setManaged(true);
    }

    private void doRegister() {
        String role = roleCombo.getValue();
        String user = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String firstName = fullNameField.getText().trim();
        String lastName = lastnameField.getText().trim();
        String pass = passwordField.getText().trim();
        String confirm = confirmField.getText().trim();

        if (role.equals("Seleccione un rol")) {
            showAlert("Error", "Por favor seleccione un rol");
            return;
        }
        if (user.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || pass.isEmpty()) {
            showAlert("Error", "Todos los campos son obligatorios");
            return;
        }
        if (!pass.equals(confirm)) {
            showAlert("Error", "Las contrase\u00F1as no coinciden");
            return;
        }
        if (pass.length() < 4) {
            showAlert("Error", "La contrase\u00F1a debe tener al menos 4 caracteres");
            return;
        }
        if (authService.usernameExists(user)) {
            showAlert("Error", "El nombre de usuario ya existe");
            return;
        }

        String fullName = firstName + " " + lastName;

        if ("Doctor".equals(role)) {
            if (!validateDoctorFields()) return;
            if (authService.register(user, email, pass, fullName, role)) {
                try {
                    String fn = convertDate(birthDateField.getText().trim());
                    Doctor d = new Doctor(null, firstName, lastName,
                        email, phoneField.getText().trim(), fn,
                        genderField.getText().trim(), addressField.getText().trim(),
                        specialtyField.getText().trim(), licenseNumberField.getText().trim(), "", 0);
                    doctorService.registerDoctor(d);
                    showAlert("\u00C9xito", "Cuenta creada correctamente. Ahora puedes iniciar sesi\u00F3n.");
                    if (mainController != null) mainController.navigateTo("login");
                } catch (Exception ex) {
                    showAlert("Error", "Error al registrar doctor: " + ex.getMessage());
                }
            } else {
                showAlert("Error", "No se pudo crear la cuenta");
            }
        } else if ("Estudiante".equals(role)) {
            if (!validateStudentFields()) return;
            if (authService.register(user, email, pass, fullName, role)) {
                try {
                    String fn = convertDate(birthDateField.getText().trim());
                    Student s = new Student(null, firstName, lastName,
                            email, phoneField.getText().trim(), fn, genderField.getText().trim(),
                            addressField.getText().trim(), "", semesterCombo.getValue(), null);
                    studentService.registerStudent(s);
                    showAlert("\u00C9xito", "Cuenta creada correctamente. Ahora puedes iniciar sesi\u00F3n.");
                    if (mainController != null) mainController.navigateTo("login");
                } catch (Exception ex) {
                    showAlert("Error", "Error al registrar estudiante: " + ex.getMessage());
                }
            } else {
                showAlert("Error", "No se pudo crear la cuenta");
            }
        } else {
            if (authService.register(user, email, pass, fullName, role)) {
                showAlert("\u00C9xito", "Cuenta creada correctamente. Ahora puedes iniciar sesi\u00F3n.");
                if (mainController != null) mainController.navigateTo("login");
            } else {
                showAlert("Error", "No se pudo crear la cuenta");
            }
        }
    }

    private boolean validateStudentFields() {
        if (isEmpty(phoneField) || isEmpty(birthDateField) || isEmpty(genderField) || isEmpty(addressField)) {
            showAlert("Error", "Complete todos los campos del estudiante");
            return false;
        }
        if (semesterCombo.getValue() == null) {
            showAlert("Error", "Seleccione un semestre");
            return false;
        }
        return true;
    }

    private boolean validateDoctorFields() {
        if (isEmpty(phoneField) || isEmpty(birthDateField) || isEmpty(genderField) || isEmpty(addressField) || isEmpty(specialtyField) || isEmpty(licenseNumberField)) {
            showAlert("Error", "Complete todos los campos del doctor");
            return false;
        }
        return true;
    }

    private boolean isEmpty(TextField f) {
        return f == null || f.getText().trim().isEmpty();
    }

    private String convertDate(String date) {
        if (date == null || date.trim().isEmpty()) return "2000-01-01";
        date = date.trim();
        if (date.matches("\\d{4}-\\d{2}-\\d{2}")) return date;
        if (date.contains("/")) {
            String[] p = date.split("/");
            if (p.length == 3) {
                String maybeYear = p[2];
                if (maybeYear.length() == 4) return String.format("%s-%s-%s", p[2], p[1], p[0]);
                if (p[0].length() == 4) return String.format("%s-%s-%s", p[0], p[1], p[2]);
            }
        }
        if (date.contains("-")) {
            String[] p = date.split("-");
            if (p.length == 3) {
                if (p[2].length() == 4) return String.format("%s-%s-%s", p[2], p[1], p[0]);
                if (p[0].length() == 4) return String.format("%s-%s-%s", p[0], p[1], p[2]);
            }
        }
        return date;
    }

    private Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial Bold", 13));
        l.setStyle("-fx-text-fill: #555;");
        return l;
    }

    private TextField createField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setPrefWidth(380);
        f.setPrefHeight(42);
        f.setStyle("-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-border-color: #ddd; -fx-font-size: 14px;");
        return f;
    }

    private PasswordField createPasswordField(String prompt) {
        PasswordField f = new PasswordField();
        f.setPromptText(prompt);
        f.setPrefWidth(380);
        f.setPrefHeight(42);
        f.setStyle("-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-border-color: #ddd; -fx-font-size: 14px;");
        return f;
    }

    private ComboBox<String> createRoleCombo() {
        ComboBox<String> cb = new ComboBox<>();
        cb.setItems(FXCollections.observableArrayList("Seleccione un rol", "Administrador", "Doctor", "Estudiante"));
        cb.setValue("Seleccione un rol");
        cb.setPrefWidth(380);
        cb.setPrefHeight(42);
        cb.setStyle("-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 5; -fx-border-color: #ddd; -fx-font-size: 14px;");
        return cb;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
