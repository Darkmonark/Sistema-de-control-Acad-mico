package com.hospital.sanrafael.controller;

import com.hospital.sanrafael.model.Doctor;
import com.hospital.sanrafael.model.Student;
import com.hospital.sanrafael.service.AlertService;
import com.hospital.sanrafael.service.DoctorService;
import com.hospital.sanrafael.service.StudentService;
import com.hospital.sanrafael.view.ViewFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class RegistryController extends BaseDashboardController {
private final StudentService studentService;
private final DoctorService doctorService;
private final AlertService alertService;
private TextField idField;
private ComboBox<String> typeCombo;
private TextArea resultArea;
private HBox searchRow;

public RegistryController(ViewFactory viewFactory) {
this.viewFactory = viewFactory;
this.studentService = new StudentService();
this.doctorService = new DoctorService();
this.alertService = AlertService.getInstance();
}

@Override protected String getSidebarColor() { return "#5DADE2"; }
@Override protected String getSidebarLogo() { return "MEDjestic"; }
@Override protected String getSidebarLetter() { return "R"; }
@Override protected String getModuleName() { return "Registros"; }
@Override protected String getModuleRole() { return "M\u00F3dulo de Ingreso"; }
@Override protected String getTitle() {
    if ("view-notifications".equals(currentSection)) return "Notificaciones";
    return "Registro de Ingreso";
}

@Override
protected VBox createSidebarMenuItems() {
VBox menu = new VBox(5);
String current = getModuleName();
boolean isAlt = "view-notifications".equals(currentSection);
Button studentsBtn = sidebarBtn("Gestion Estudiantes", current.equals("Estudiantes") && !isAlt);
Button doctorsBtn = sidebarBtn("Gestion Doctores", current.equals("Doctores") && !isAlt);
Button subjectsBtn = sidebarBtn("Materias", current.equals("Materias") && !isAlt);
Button schedulesBtn = sidebarBtn("Horarios", current.equals("Horarios") && !isAlt);
Button recordsBtn = sidebarBtn("Registros", current.equals("Registros") && !isAlt);
Button requestsBtn = sidebarBtn("Solicitudes Cambio", false);
studentsBtn.setOnAction(e -> { if (mainController != null) mainController.navigateTo("students"); });
doctorsBtn.setOnAction(e -> { if (mainController != null) mainController.navigateTo("doctors"); });
subjectsBtn.setOnAction(e -> { if (mainController != null) mainController.navigateTo("subjects"); });
schedulesBtn.setOnAction(e -> { if (mainController != null) mainController.navigateTo("schedules"); });
recordsBtn.setOnAction(e -> { if (mainController != null) mainController.navigateTo("records"); });
requestsBtn.setOnAction(e -> { if (mainController != null) mainController.navigateTo("change-requests"); });
menu.getChildren().addAll(studentsBtn, doctorsBtn, subjectsBtn, schedulesBtn, recordsBtn, requestsBtn);
return menu;
}

@Override
protected VBox createContent() {
VBox content = new VBox(20);
content.setPadding(new Insets(25));

if ("view-notifications".equals(currentSection)) {
    content.getChildren().add(createAdminNotificationsSection());
    return content;
}
content.setStyle("-fx-background-color: #f0f4f8;");

HBox stats = new HBox(15);
int totalEst = studentService.getAllStudents().size();
int totalDoc = doctorService.getAllDoctors().size();
stats.getChildren().addAll(
statCard("ESTUDIANTES", String.valueOf(totalEst), "#2C3E8F"),
statCard("DOCTORES", String.valueOf(totalDoc), "#27AE60"),
statCard("TOTAL", String.valueOf(totalEst + totalDoc), "#E67E22"),
statCard("ESTADO", "Activo", "#E74C3C")
);

VBox searchSection = new VBox(10);
searchSection.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

Label sectionTitle = new Label("Buscar Persona");
sectionTitle.setFont(Font.font("Arial Bold", 16));
sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

HBox searchRow = new HBox(12);
searchRow.setAlignment(Pos.CENTER_LEFT);

Label typeLabel = new Label("Tipo:");
typeLabel.setFont(Font.font("Arial", 13));
typeLabel.setStyle("-fx-text-fill: #555;");

typeCombo = new ComboBox<>();
typeCombo.getItems().addAll("Estudiante", "Doctor");
typeCombo.setValue("Estudiante");
typeCombo.setStyle("-fx-font-size: 13px; -fx-padding: 4;");

Label idLabel = new Label("ID:");
idLabel.setFont(Font.font("Arial", 13));
idLabel.setStyle("-fx-text-fill: #555;");

idField = new TextField();
idField.setPrefWidth(200);
idField.setPromptText("Ingrese ID");
idField.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ddd; -fx-font-size: 13px;");

Button searchBtn = actionBtn("Buscar", "#2C3E8F");
searchBtn.setOnAction(e -> search());
idField.setOnAction(e -> search());

searchRow.getChildren().addAll(typeLabel, typeCombo, idLabel, idField, searchBtn);

resultArea = new TextArea();
resultArea.setPrefHeight(250);
resultArea.setEditable(false);
resultArea.setFont(Font.font("Consolas", 12));
resultArea.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ddd; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10;");

searchSection.getChildren().addAll(sectionTitle, searchRow, resultArea);
content.getChildren().addAll(stats, searchSection);

return content;
}

private void search() {
String id = idField.getText().trim();
if (id.isEmpty()) {
show("Error", "Ingrese un ID");
return;
}

StringBuilder sb = new StringBuilder();
if (typeCombo.getValue().equals("Estudiante")) {
Student est = studentService.getStudentById(id);
if (est == null) {
show("Error", "Estudiante no encontrado");
resultArea.clear();
return;
}

Doctor assignedDoctor = null;
if (est.getAssignedDoctorId() != null) {
assignedDoctor = doctorService.getDoctorById(est.getAssignedDoctorId());
}

boolean canEnter = alertService.validateStudentEntry(est, assignedDoctor);

if (!canEnter) {
sb.append("\n");
sb.append(" ALERTA: INGRESO DENEGADO\n");
sb.append("\n\n");
sb.append(" El estudiante ").append(est.getFullName()).append(" (").append(est.getId()).append(") no puede ingresar.\n");
sb.append(" Raz\u00F3n: ").append("Verifique las alertas generadas.\n");
} else {
sb.append("\n");
sb.append(" INGRESO AUTORIZADO\n");
sb.append("\n\n");
sb.append(" ID: ").append(est.getId()).append("\n");
sb.append(" Nombre: ").append(est.getFullName()).append("\n");
sb.append(" Email: ").append(est.getEmail()).append("\n");
sb.append(" Tel\u00E9fono: ").append(est.getPhone()).append("\n");
sb.append(" Carrera: ").append(est.getCareer()).append("\n");
sb.append(" Semestre: ").append(est.getSemester()).append("\u00B0\n");
sb.append(" Turno: ").append(est.getShift()).append("\n");
sb.append(" Materias inscritas: ").append(est.getSubjects().size()).append("\n");
sb.append(" Estado: INGRESO PERMITIDO\n");
}

Button exitBtn = actionBtn("Registrar Salida", "#E74C3C");
exitBtn.setOnAction(e -> {
alertService.registerExit(est);
show("Registro", "Salida registrada para " + est.getFullName());
});

if (canEnter) {
searchRow.getChildren().add(exitBtn);
}
} else {
Doctor doc = doctorService.getDoctorById(id);
if (doc == null) {
show("Error", "Doctor no encontrado");
resultArea.clear();
return;
}
sb.append("\n");
sb.append(" DATOS DEL DOCTOR\n");
sb.append("\n\n");
sb.append(" ID: ").append(doc.getId()).append("\n");
sb.append(" Nombre: ").append(doc.getFullName()).append("\n");
sb.append(" Email: ").append(doc.getEmail()).append("\n");
sb.append(" Tel\u00E9fono: ").append(doc.getPhone()).append("\n");
sb.append(" Especialidad: ").append(doc.getSpecialty()).append("\n");
sb.append(" N\u00B0 Colegiado: ").append(doc.getLicenseNumber()).append("\n");
sb.append(" \u00C1rea: ").append(doc.getAssignedArea()).append("\n");
sb.append(" A\u00F1os Experiencia: ").append(doc.getYearsExperience()).append("\n");
sb.append(" Estudiantes supervisados: ").append(doc.getAssignedStudents().size()).append("\n");
}
sb.append("\n\n");
sb.append(" Registro consultado exitosamente\n");
sb.append("\n");
resultArea.setText(sb.toString());
}
}
