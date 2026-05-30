package com.hospital.sanrafael.controller;

import com.hospital.sanrafael.model.DataChangeRequest;
import com.hospital.sanrafael.model.Doctor;
import com.hospital.sanrafael.service.DataChangeRequestService;
import com.hospital.sanrafael.service.DoctorService;
import com.hospital.sanrafael.view.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.util.List;
import java.util.Map;

public class DoctorController extends BaseDashboardController {
    private final DoctorService doctorService;
    private final DataChangeRequestService changeRequestService;
    private TableView<Doctor> tableView;
    private TextField idField, firstNameField, lastNameField, emailField, phoneField;
    private TextField birthDateField, genderField, addressField;
    private TextField specialtyField, licenseField, areaField, experienceField;

    public DoctorController(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
        this.doctorService = new DoctorService();
        this.changeRequestService = DataChangeRequestService.getInstance();
    }

    @Override protected String getSidebarColor() { return "#2C3E8F"; }
    @Override protected String getSidebarLogo() { return "MEDjestic"; }
    @Override protected String getSidebarLetter() { return "D"; }
    @Override protected String getModuleName() { return "Doctores"; }
    @Override protected String getModuleRole() { return "M\u00F3dulo M\u00E9dico"; }
    @Override protected String getTitle() {
        if ("requests".equals(currentSection)) return "Solicitudes de Cambio de Datos";
        if ("view-notifications".equals(currentSection)) return "Notificaciones";
        return "Gesti\u00F3n de Doctores";
    }

    @Override
    protected VBox createSidebarMenuItems() {
        VBox menu = new VBox(5);
        String current = getModuleName();
        boolean isAlt = "requests".equals(currentSection) || "view-notifications".equals(currentSection);
        Button studentsBtn = sidebarBtn("Gestion Estudiantes", current.equals("Estudiantes") && !isAlt);
        Button doctorsBtn = sidebarBtn("Gestion Doctores", current.equals("Doctores") && !isAlt);
        Button subjectsBtn = sidebarBtn("Materias", current.equals("Materias") && !isAlt);
        Button schedulesBtn = sidebarBtn("Horarios", current.equals("Horarios") && !isAlt);
        Button recordsBtn = sidebarBtn("Registros", current.equals("Registros") && !isAlt);
        Button requestsBtn = sidebarBtn("Solicitudes Cambio", "requests".equals(currentSection));
        studentsBtn.setOnAction(e -> { if (mainController != null) mainController.navigateTo("students"); });
        doctorsBtn.setOnAction(e -> { if (mainController != null) mainController.navigateTo("doctors"); });
        subjectsBtn.setOnAction(e -> { if (mainController != null) mainController.navigateTo("subjects"); });
        schedulesBtn.setOnAction(e -> { if (mainController != null) mainController.navigateTo("schedules"); });
        recordsBtn.setOnAction(e -> { if (mainController != null) mainController.navigateTo("records"); });
        requestsBtn.setOnAction(e -> { currentSection = "requests"; refreshContent(); });
        menu.getChildren().addAll(studentsBtn, doctorsBtn, subjectsBtn, schedulesBtn, recordsBtn, requestsBtn);
        return menu;
    }

    @Override
    protected VBox createContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: #f0f4f8;");

        if ("view-notifications".equals(currentSection)) {
            content.getChildren().add(createAdminNotificationsSection());
            return content;
        }
        if ("requests".equals(currentSection)) {
            content.getChildren().add(createRequestsManagementSection());
            return content;
        }

        HBox stats = new HBox(15);
        int total = doctorService.getAllDoctors().size();
        stats.getChildren().addAll(
            statCard("TOTAL", String.valueOf(total), "#2C3E8F"),
            statCard("ESPECIALIDADES", "8", "#27AE60"),
            statCard("\u00C1REAS", "4", "#E67E22"),
            statCard("EXPERIENCIA", "5+ a\u00F1os", "#E74C3C")
        );

        VBox tableSection = new VBox(10);
        tableSection.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label sectionTitle = new Label("Lista de Doctores");
        sectionTitle.setFont(Font.font("Arial Bold", 16));
        sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

        tableView = new TableView<>();
        tableView.setItems(getDoctorsData());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefHeight(180);
        createColumns();

        VBox formSection = new VBox(10);
        formSection.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-padding: 15;");

        Label formTitle = new Label("Formulario de Doctor");
        formTitle.setFont(Font.font("Arial Bold", 14));
        formTitle.setStyle("-fx-text-fill: #2c3e50;");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        idField = createField();
        firstNameField = createField();
        lastNameField = createField();
        emailField = createField();
        phoneField = createField();
        birthDateField = createField();
        genderField = createField();
        addressField = createField();
        specialtyField = createField();
        licenseField = createField();
        areaField = createField();
        experienceField = createField();

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

HBox buttons = new HBox(12);
buttons.setAlignment(Pos.CENTER);
buttons.setPadding(new Insets(10, 0, 0, 0));

Button saveBtn = actionBtn("Guardar", "#27AE60");
Button deleteBtn = actionBtn("Eliminar", "#E74C3C");
Button clearBtn = actionBtn("Limpiar", "#95A5A6");

saveBtn.setOnAction(e -> save());
deleteBtn.setOnAction(e -> delete());
clearBtn.setOnAction(e -> clearForm());

buttons.getChildren().addAll(saveBtn, deleteBtn, clearBtn);
        formSection.getChildren().addAll(formTitle, grid, buttons);
        tableSection.getChildren().addAll(sectionTitle, tableView, formSection);
        content.getChildren().addAll(stats, tableSection);

        tableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, sel) -> { if (sel != null) fillForm(sel); }
        );

        return content;
    }

    private VBox createRequestsManagementSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label title = new Label("Solicitudes de Cambio de Datos");
        title.setFont(Font.font("Arial Bold", 16));
        title.setStyle("-fx-text-fill: #2c3e50;");

        List<DataChangeRequest> pending = changeRequestService.getPendingRequests();

        if (pending.isEmpty()) {
            Label noPending = new Label("No hay solicitudes pendientes de revisi\u00F3n");
            noPending.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            section.getChildren().addAll(title, noPending);
            return section;
        }

        Label info = new Label("Solicitudes pendientes: " + pending.size());
        info.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
        section.getChildren().addAll(title, info);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox requestList = new VBox(10);
        requestList.setPadding(new Insets(10, 0, 0, 0));

        for (DataChangeRequest req : pending) {
            VBox reqBox = new VBox(8);
            reqBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 8;");

            Label whoLabel = new Label("Solicitante: " + req.getRequesterName() + " (" + req.getRequesterRole() + ") - ID: " + req.getEntityId());
            whoLabel.setFont(Font.font("Arial Bold", 13));
            whoLabel.setStyle("-fx-text-fill: #2c3e50;");

            Label dateLabel = new Label("Fecha: " + req.getRequestDate());
            dateLabel.setFont(Font.font("Arial", 11));
            dateLabel.setStyle("-fx-text-fill: #999;");

            VBox changesBox = new VBox(3);
            changesBox.setPadding(new Insets(5, 0, 5, 10));
            Label changesTitle = new Label("Cambios solicitados:");
            changesTitle.setFont(Font.font("Arial Bold", 12));
            changesTitle.setStyle("-fx-text-fill: #555;");
            changesBox.getChildren().add(changesTitle);

            for (Map.Entry<String, String> entry : req.getProposedData().entrySet()) {
                String fieldName = entry.getKey();
                String newValue = entry.getValue();
                String oldValue = req.getOriginalData().get(fieldName);
                if (oldValue == null) oldValue = "";
                if (!oldValue.equals(newValue)) {
                    Label changeLabel = new Label("  " + fieldName + ": '" + oldValue + "' -> '" + newValue + "'");
                    changeLabel.setFont(Font.font("Arial", 11));
                    changeLabel.setStyle("-fx-text-fill: #2C3E8F;");
                    changesBox.getChildren().add(changeLabel);
                }
            }

            TextArea reasonArea = new TextArea();
            reasonArea.setPromptText("Motivo (requerido si se deniega)...");
            reasonArea.setPrefHeight(60);

            HBox actionButtons = new HBox(10);
            actionButtons.setAlignment(Pos.CENTER);

            Button approveBtn = actionBtn("Aprobar", "#27AE60");
            Button denyBtn = actionBtn("Denegar", "#E74C3C");

            approveBtn.setOnAction(e -> {
                String msg = reasonArea.getText().trim();
                changeRequestService.approveRequest(req.getId(), msg);
                show("Solicitud Aprobada", "Los cambios han sido aplicados correctamente.");
                refreshContent();
            });

            denyBtn.setOnAction(e -> {
                String reason = reasonArea.getText().trim();
                if (reason.isEmpty()) {
                    show("Error", "Debe escribir un motivo para denegar la solicitud.");
                    return;
                }
                changeRequestService.denyRequest(req.getId(), reason);
                show("Solicitud Denegada", "La solicitud ha sido denegada. Se notific\u00F3 al solicitante.");
                refreshContent();
            });

            actionButtons.getChildren().addAll(approveBtn, denyBtn);
            reqBox.getChildren().addAll(whoLabel, dateLabel, changesBox, reasonArea, actionButtons);
            requestList.getChildren().add(reqBox);
        }

        scrollPane.setContent(requestList);
        section.getChildren().add(scrollPane);
        return section;
    }

    private void createColumns() {
        TableColumn<Doctor, String> c1 = new TableColumn<>("ID");
        c1.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getId()));
        TableColumn<Doctor, String> c2 = new TableColumn<>("Nombre");
        c2.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getFullName()));
        TableColumn<Doctor, String> c3 = new TableColumn<>("Especialidad");
        c3.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getSpecialty()));
        TableColumn<Doctor, String> c4 = new TableColumn<>("N\u00B0 Colegiado");
        c4.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getLicenseNumber()));
        TableColumn<Doctor, String> c5 = new TableColumn<>("\u00C1rea");
        c5.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getAssignedArea()));
        tableView.getColumns().addAll(c1, c2, c3, c4, c5);
    }

    private void fillForm(Doctor d) {
        idField.setText(d.getId());
        firstNameField.setText(d.getFirstName());
        lastNameField.setText(d.getLastName());
        emailField.setText(d.getEmail());
        phoneField.setText(d.getPhone());
        birthDateField.setText(d.getBirthDate());
        genderField.setText(d.getGender());
        addressField.setText(d.getAddress());
        specialtyField.setText(d.getSpecialty());
        licenseField.setText(d.getLicenseNumber());
        areaField.setText(d.getAssignedArea());
        experienceField.setText(String.valueOf(d.getYearsExperience()));
    }

    private void clearForm() {
        idField.clear(); firstNameField.clear(); lastNameField.clear(); emailField.clear();
        phoneField.clear(); birthDateField.clear(); genderField.clear();
        addressField.clear(); specialtyField.clear(); licenseField.clear();
        areaField.clear(); experienceField.clear();
    }

    private void save() {
        try {
            if (fieldsEmpty()) {
                show("Error", "Complete todos los campos obligatorios");
                return;
            }
            Doctor d = new Doctor(null, firstNameField.getText(), lastNameField.getText(),
                    emailField.getText(), phoneField.getText(), convertDate(birthDateField.getText()), genderField.getText(),
                    addressField.getText(), specialtyField.getText(), licenseField.getText(),
                    areaField.getText(), parseInt(experienceField.getText(), 0));
            doctorService.registerDoctor(d);
            tableView.setItems(getDoctorsData());
            clearForm();
            show("\u00C9xito", "Doctor registrado");
        } catch (Exception ex) {
            show("Error", "Error: " + ex.getMessage());
        }
    }

private void delete() {
        try {
            String id = idField.getText();
            if (id == null || id.isEmpty()) {
                show("Error", "Seleccione un doctor de la tabla");
                return;
            }
            doctorService.deleteDoctor(id);
            tableView.setItems(getDoctorsData());
            clearForm();
            show("\u00C9xito", "Doctor eliminado");
        } catch (Exception ex) {
            show("Error", "Error: " + ex.getMessage());
        }
    }

    private boolean fieldsEmpty() {
        return firstNameField.getText().trim().isEmpty()
            || lastNameField.getText().trim().isEmpty()
            || emailField.getText().trim().isEmpty()
            || phoneField.getText().trim().isEmpty()
            || specialtyField.getText().trim().isEmpty();
    }

    private int parseInt(String val, int def) {
        if (val == null || val.trim().isEmpty()) return def;
        try { return Integer.parseInt(val.trim()); } catch (NumberFormatException e) { return def; }
    }

    private String convertDate(String date) {
        if (date == null || date.trim().isEmpty()) return "";
        date = date.trim();
        if (date.matches("\\d{4}-\\d{2}-\\d{2}")) return date;
        if (date.contains("/")) {
            String[] p = date.split("/");
            if (p.length == 3) return String.format("%s-%s-%s", p[2], p[1], p[0]);
        }
        return date;
    }

    private ObservableList<Doctor> getDoctorsData() {
        return FXCollections.observableArrayList(doctorService.getAllDoctors());
    }
}
