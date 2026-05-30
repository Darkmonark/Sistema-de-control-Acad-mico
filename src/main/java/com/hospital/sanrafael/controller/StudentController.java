package com.hospital.sanrafael.controller;

import com.hospital.sanrafael.model.DataChangeRequest;
import com.hospital.sanrafael.model.Shift;
import com.hospital.sanrafael.model.Student;
import com.hospital.sanrafael.service.AlertService;
import com.hospital.sanrafael.service.DataChangeRequestService;
import com.hospital.sanrafael.service.StudentService;
import com.hospital.sanrafael.view.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class StudentController extends BaseDashboardController {
private final StudentService studentService;
private final AlertService alertService;
private final DataChangeRequestService changeRequestService;
private TableView<Student> tableView;
private TextField idField, firstNameField, lastNameField, emailField, phoneField;
private TextField birthDateField, genderField, addressField;
private TextField careerField, semesterField;
private ComboBox<Shift> shiftField;
private TextField arlDateField;

public StudentController(ViewFactory viewFactory) {
this.viewFactory = viewFactory;
this.studentService = new StudentService();
this.alertService = AlertService.getInstance();
this.changeRequestService = DataChangeRequestService.getInstance();
}

    @Override protected String getSidebarColor() { return "#27AE60"; }
    @Override protected String getSidebarLogo() { return "MEDjestic"; }
    @Override protected String getSidebarLetter() { return "E"; }
    @Override protected String getModuleName() { return "Estudiantes"; }
    @Override protected String getModuleRole() { return "M\u00F3dulo Acad\u00E9mico"; }
    @Override protected String getTitle() {
        if ("requests".equals(currentSection)) return "Solicitudes de Cambio de Datos";
        if ("view-notifications".equals(currentSection)) return "Notificaciones";
        return "Gesti\u00F3n de Estudiantes";
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
int total = studentService.getAllStudents().size();
stats.getChildren().addAll(
statCard("TOTAL", String.valueOf(total), "#2C3E8F"),
statCard("SEMESTRES", "10", "#27AE60"),
statCard("CARRERAS", "5", "#E67E22"),
statCard("TURNOS", "Ma\u00F1ana/Tarde/Noche", "#E74C3C")
);

Button checkArlBtn = actionBtn("Verificar ARLs", "#E67E22");
checkArlBtn.setOnAction(e -> checkArlExpirations());
stats.getChildren().add(checkArlBtn);

        VBox tableSection = new VBox(10);
        tableSection.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label sectionTitle = new Label("Lista de Estudiantes");
        sectionTitle.setFont(Font.font("Arial Bold", 16));
        sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

        tableView = new TableView<>();
        tableView.setItems(getStudentsData());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefHeight(180);
        createColumns();

        VBox formSection = new VBox(10);
        formSection.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-padding: 15;");

        Label formTitle = new Label("Formulario de Estudiante");
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
        careerField = createField();
        semesterField = createField();
        shiftField = new ComboBox<>();
        shiftField.setPrefWidth(180);
        shiftField.getItems().setAll(Shift.values());
        shiftField.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 4; -fx-border-color: #ddd; -fx-font-size: 13px;");

        int r = 0;
        grid.add(fieldLabel("Nombre:"), 0, r); grid.add(firstNameField, 1, r);
        grid.add(fieldLabel("Apellido:"), 2, r); grid.add(lastNameField, 3, r++);
        grid.add(fieldLabel("Email:"), 0, r); grid.add(emailField, 1, r);
        grid.add(fieldLabel("Tel\u00E9fono:"), 2, r); grid.add(phoneField, 3, r++);
        grid.add(fieldLabel("Fecha Nac.:"), 0, r); grid.add(birthDateField, 1, r);
        grid.add(fieldLabel("G\u00E9nero:"), 2, r); grid.add(genderField, 3, r++);
        grid.add(fieldLabel("Direcci\u00F3n:"), 0, r); grid.add(addressField, 1, r);
        grid.add(fieldLabel("Carrera:"), 2, r); grid.add(careerField, 3, r++);
grid.add(fieldLabel("Semestre:"), 0, r); grid.add(semesterField, 1, r);
grid.add(fieldLabel("Turno:"), 2, r); grid.add(shiftField, 3, r++);
grid.add(fieldLabel("Vencimiento ARL:"), 0, r); grid.add(arlDateField = createField(), 1, r++);

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button saveBtn = actionBtn("Guardar", "#27AE60");
        Button updateBtn = actionBtn("Actualizar", "#F39C12");
        Button deleteBtn = actionBtn("Eliminar", "#E74C3C");
        Button clearBtn = actionBtn("Limpiar", "#95A5A6");

        saveBtn.setOnAction(e -> save());
        updateBtn.setOnAction(e -> update());
        deleteBtn.setOnAction(e -> delete());
        clearBtn.setOnAction(e -> clearForm());

        buttons.getChildren().addAll(saveBtn, updateBtn, deleteBtn, clearBtn);
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
            Label noPending = new Label("No hay solicitudes pendientes de revisión");
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
                show("Solicitud Denegada", "La solicitud ha sido denegada. Se notificó al solicitante.");
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
        TableColumn<Student, String> c1 = new TableColumn<>("ID");
        c1.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getId()));
        TableColumn<Student, String> c2 = new TableColumn<>("Nombre");
        c2.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getFullName()));
        TableColumn<Student, String> c3 = new TableColumn<>("Carrera");
        c3.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCareer()));
        TableColumn<Student, Number> c4 = new TableColumn<>("Semestre");
        c4.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getSemester()));
        TableColumn<Student, String> c5 = new TableColumn<>("Email");
        c5.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getEmail()));
        tableView.getColumns().addAll(c1, c2, c3, c4, c5);
    }

private void fillForm(Student s) {
idField.setText(s.getId());
firstNameField.setText(s.getFirstName());
lastNameField.setText(s.getLastName());
emailField.setText(s.getEmail());
phoneField.setText(s.getPhone());
birthDateField.setText(s.getBirthDate());
genderField.setText(s.getGender());
addressField.setText(s.getAddress());
careerField.setText(s.getCareer());
semesterField.setText(String.valueOf(s.getSemester()));
shiftField.setValue(s.getShift());
if (s.getArlExpirationDate() != null) {
arlDateField.setText(s.getArlExpirationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
} else {
arlDateField.setText("");
}
}

private void clearForm() {
idField.clear(); firstNameField.clear(); lastNameField.clear(); emailField.clear();
phoneField.clear(); birthDateField.clear(); genderField.clear();
addressField.clear(); careerField.clear(); semesterField.clear(); shiftField.setValue(null);
arlDateField.clear();
}

    private void save() {
        try {
            if (fieldsEmpty()) {
                show("Error", "Complete todos los campos obligatorios");
                return;
            }
            if (shiftField.getValue() == null) {
                show("Error", "Seleccione un turno");
                return;
            }
String fn = convertDate(birthDateField.getText());
Student s = new Student(null, firstNameField.getText(), lastNameField.getText(),
emailField.getText(), phoneField.getText(), fn, genderField.getText(),
addressField.getText(), careerField.getText(), parseInt(semesterField.getText(), 1), shiftField.getValue());
if (arlDateField.getText() != null && !arlDateField.getText().trim().isEmpty()) {
s.setArlExpirationDate(parseDate(arlDateField.getText()));
}
studentService.registerStudent(s);
tableView.setItems(getStudentsData());
clearForm();
show("\u00C9xito", "Estudiante registrado");
        } catch (Exception ex) {
            show("Error", "Error: " + ex.getMessage());
        }
    }

    private void update() {
        try {
            if (idField.getText() == null || idField.getText().isEmpty()) {
                show("Error", "Seleccione un estudiante de la tabla");
                return;
            }
            if (shiftField.getValue() == null) {
                show("Error", "Seleccione un turno");
                return;
            }
Student s = new Student(idField.getText(), firstNameField.getText(), lastNameField.getText(),
emailField.getText(), phoneField.getText(), convertDate(birthDateField.getText()), genderField.getText(),
addressField.getText(), careerField.getText(), parseInt(semesterField.getText(), 1), shiftField.getValue());
if (arlDateField.getText() != null && !arlDateField.getText().trim().isEmpty()) {
s.setArlExpirationDate(parseDate(arlDateField.getText()));
}
studentService.updateStudent(s);
tableView.setItems(getStudentsData());
show("\u00C9xito", "Estudiante actualizado");
        } catch (Exception ex) {
            show("Error", "Error: " + ex.getMessage());
        }
    }

    private void delete() {
        try {
            String id = idField.getText();
            if (id == null || id.isEmpty()) {
                show("Error", "Seleccione un estudiante de la tabla");
                return;
            }
            studentService.deleteStudent(id);
            tableView.setItems(getStudentsData());
            clearForm();
            show("\u00C9xito", "Estudiante eliminado");
        } catch (Exception ex) {
            show("Error", "Error: " + ex.getMessage());
        }
    }

private boolean fieldsEmpty() {
return firstNameField.getText().trim().isEmpty()
|| lastNameField.getText().trim().isEmpty()
|| emailField.getText().trim().isEmpty()
|| phoneField.getText().trim().isEmpty()
|| careerField.getText().trim().isEmpty();
}

private void checkArlExpirations() {
studentService.checkAllArlExpirations();
show("\u00C9xito", "Verificaci\u00F3n de ARLs completada. Revision las notificaciones.");
}

private int parseInt(String val, int def) {
if (val == null || val.trim().isEmpty()) return def;
try { return Integer.parseInt(val.trim()); } catch (NumberFormatException e) { return def; }
}

private LocalDate parseDate(String date) {
if (date == null || date.trim().isEmpty()) return null;
date = date.trim();
try {
if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
return LocalDate.parse(date);
}
if (date.contains("/")) {
String[] p = date.split("/");
if (p.length == 3) {
return LocalDate.of(Integer.parseInt(p[2]), Integer.parseInt(p[1]), Integer.parseInt(p[0]));
}
}
} catch (Exception e) {
show("Error", "Fecha inv\u00E1lida: " + date);
}
return null;
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

    private ObservableList<Student> getStudentsData() {
        return FXCollections.observableArrayList(studentService.getAllStudents());
    }
}
