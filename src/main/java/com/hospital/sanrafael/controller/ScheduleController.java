package com.hospital.sanrafael.controller;

import com.hospital.sanrafael.model.Doctor;
import com.hospital.sanrafael.model.Student;
import com.hospital.sanrafael.model.Schedule;
import com.hospital.sanrafael.service.DoctorService;
import com.hospital.sanrafael.service.StudentService;
import com.hospital.sanrafael.view.ViewFactory;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ScheduleController extends BaseDashboardController {
    private final StudentService studentService;
    private final DoctorService doctorService;
    private TableView<Schedule> tableView;
    private ComboBox<String> typeCombo;
    private TextField idField;
    private DatePicker dayPicker;
    private TextField startTimeField, endTimeField;
    private TextField activityField, responsibleField, classroomField;
    private Label personInfoLabel;
    private String currentPersonId;
    private boolean isDoctorMode;

    public ScheduleController(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
        this.studentService = new StudentService();
        this.doctorService = new DoctorService();
    }

    @Override protected String getSidebarColor() { return "#E74C3C"; }
    @Override protected String getSidebarLogo() { return "MEDjestic"; }
    @Override protected String getSidebarLetter() { return "H"; }
    @Override protected String getModuleName() { return "Horarios"; }
    @Override protected String getModuleRole() { return "Gesti\u00F3n de Horarios"; }
    @Override protected String getTitle() {
        if ("view-notifications".equals(currentSection)) return "Notificaciones";
        return "Gesti\u00F3n de Horarios";
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
        content.setStyle("-fx-background-color: #f0f4f8;");

        if ("view-notifications".equals(currentSection)) {
            content.getChildren().add(createAdminNotificationsSection());
            return content;
        }

        HBox searchRow = new HBox(12);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        searchRow.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label typeLabel = new Label("Tipo:");
        typeLabel.setFont(Font.font("Arial", 13));
        typeLabel.setStyle("-fx-text-fill: #555;");

        typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Estudiante", "Doctor");
        typeCombo.setValue("Estudiante");
        typeCombo.setStyle("-fx-font-size: 13px; -fx-padding: 4;");
        isDoctorMode = false;

        Label idLabel = new Label("ID:");
        idLabel.setFont(Font.font("Arial", 13));
        idLabel.setStyle("-fx-text-fill: #555;");

        idField = new TextField();
        idField.setPrefWidth(180);
        idField.setPromptText("Ingrese ID");
        idField.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ddd; -fx-font-size: 13px;");

        Button searchBtn = actionBtn("Buscar", "#E74C3C");
        searchBtn.setOnAction(e -> searchPerson());
        idField.setOnAction(e -> searchPerson());

        personInfoLabel = new Label("Seleccione un tipo e ID para consultar horarios");
        personInfoLabel.setFont(Font.font("Arial", 13));
        personInfoLabel.setStyle("-fx-text-fill: #999;");

        searchRow.getChildren().addAll(typeLabel, typeCombo, idLabel, idField, searchBtn, personInfoLabel);

        VBox tableSection = new VBox(10);
        tableSection.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label sectionTitle = new Label("Horario Semanal");
        sectionTitle.setFont(Font.font("Arial Bold", 16));
        sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefHeight(150);
        createTableColumns();

        VBox formSection = new VBox(10);
        formSection.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-padding: 15;");

        Label formTitle = new Label("Agregar Horario");
        formTitle.setFont(Font.font("Arial Bold", 14));
        formTitle.setStyle("-fx-text-fill: #2c3e50;");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        dayPicker = new DatePicker();
        dayPicker.setValue(LocalDate.now());
        dayPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        startTimeField = createField();
        endTimeField = createField();
        activityField = createField();
        responsibleField = createField();
        classroomField = createField();

        int r = 0;
        grid.add(fieldLabel("D\u00EDa:"), 0, r); grid.add(dayPicker, 1, r++);
        grid.add(fieldLabel("Hora Inicio:"), 0, r); grid.add(startTimeField, 1, r);
        grid.add(fieldLabel("Hora Fin:"), 2, r); grid.add(endTimeField, 3, r++);
        grid.add(fieldLabel("Actividad:"), 0, r); grid.add(activityField, 1, r);
        grid.add(fieldLabel("Responsable:"), 2, r); grid.add(responsibleField, 3, r++);
        grid.add(fieldLabel("Aula:"), 0, r); grid.add(classroomField, 1, r++);

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button addBtn = actionBtn("Agregar", "#27AE60");
        Button deleteBtn = actionBtn("Eliminar", "#E74C3C");
        Button clearBtn = actionBtn("Limpiar", "#95A5A6");

        addBtn.setOnAction(e -> addSchedule());
        deleteBtn.setOnAction(e -> removeSchedule());
        clearBtn.setOnAction(e -> clearFormSchedule());

        buttons.getChildren().addAll(addBtn, deleteBtn, clearBtn);
        formSection.getChildren().addAll(formTitle, grid, buttons);
        tableSection.getChildren().addAll(sectionTitle, tableView, formSection);
        content.getChildren().addAll(searchRow, tableSection);

        return content;
    }

    private void createTableColumns() {
        TableColumn<Schedule, String> c1 = new TableColumn<>("D\u00EDa");
        c1.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDay()));
        TableColumn<Schedule, String> c2 = new TableColumn<>("Inicio");
        c2.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStartTime()));
        TableColumn<Schedule, String> c3 = new TableColumn<>("Fin");
        c3.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getEndTime()));
        TableColumn<Schedule, String> c4 = new TableColumn<>("Actividad");
        c4.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getActivity()));
        TableColumn<Schedule, String> c5 = new TableColumn<>("Responsable");
        c5.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getResponsible()));
        TableColumn<Schedule, String> c6 = new TableColumn<>("Aula");
        c6.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getClassroom()));
        tableView.getColumns().addAll(c1, c2, c3, c4, c5, c6);
    }

    private void searchPerson() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            show("Error", "Ingrese un ID");
            return;
        }

        isDoctorMode = typeCombo.getValue().equals("Doctor");
        currentPersonId = id;

        if (isDoctorMode) {
            Doctor doc = doctorService.getDoctorById(id);
            if (doc == null) {
                show("Error", "No se encontr\u00F3 Doctor con ID: " + id);
                tableView.setItems(FXCollections.observableArrayList());
                personInfoLabel.setText("Doctor no encontrado");
                return;
            }
            personInfoLabel.setText("Dr. " + doc.getFullName() + "  " + doc.getSpecialty());
            responsibleField.setText("Dr. " + doc.getFullName());
            tableView.setItems(FXCollections.observableArrayList(doc.getCareSchedule()));
        } else {
            Student est = studentService.getStudentById(id);
            if (est == null) {
                show("Error", "No se encontr\u00F3 Estudiante con ID: " + id);
                tableView.setItems(FXCollections.observableArrayList());
                personInfoLabel.setText("Estudiante no encontrado");
                return;
            }
            personInfoLabel.setText(est.getFullName() + "  " + est.getCareer() + " (" + est.getSemester() + "\u00B0 Semestre)");
            responsibleField.setText(est.getFullName());
            tableView.setItems(FXCollections.observableArrayList(est.getWeeklySchedule()));
        }
    }

    private void addSchedule() {
        if (currentPersonId == null || currentPersonId.isEmpty()) {
            show("Error", "Primero busque una persona");
            return;
        }

        try {
            LocalDate selected = dayPicker.getValue();
            if (selected == null) {
                show("Error", "Seleccione una fecha");
                return;
            }
            Schedule h = new Schedule(
                selected.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                startTimeField.getText(), endTimeField.getText(),
                activityField.getText(), responsibleField.getText(), classroomField.getText()
            );

            if (isDoctorMode) {
                Doctor doc = doctorService.getDoctorById(currentPersonId);
                if (doc != null) {
                    doctorService.addCareSchedule(doc, h);
                    tableView.setItems(FXCollections.observableArrayList(doc.getCareSchedule()));
                }
            } else {
                Student est = studentService.getStudentById(currentPersonId);
                if (est != null) {
                    est.addSchedule(h);
                    tableView.setItems(FXCollections.observableArrayList(est.getWeeklySchedule()));
                }
            }
            clearFormSchedule();
            show("\u00C9xito", "Horario agregado");
        } catch (Exception ex) {
            show("Error", "Error: " + ex.getMessage());
        }
    }

    private void removeSchedule() {
        if (currentPersonId == null || currentPersonId.isEmpty()) {
            show("Error", "Primero busque una persona");
            return;
        }

        Schedule selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            show("Error", "Seleccione un horario de la tabla");
            return;
        }

        try {
            if (isDoctorMode) {
                Doctor doc = doctorService.getDoctorById(currentPersonId);
                if (doc != null) {
                    doctorService.removeCareSchedule(doc, selected);
                    tableView.setItems(FXCollections.observableArrayList(doc.getCareSchedule()));
                }
            } else {
                Student est = studentService.getStudentById(currentPersonId);
                if (est != null) {
                    est.getWeeklySchedule().remove(selected);
                    tableView.setItems(FXCollections.observableArrayList(est.getWeeklySchedule()));
                }
            }
            show("\u00C9xito", "Horario eliminado");
        } catch (Exception ex) {
            show("Error", "Error: " + ex.getMessage());
        }
    }

    private void clearFormSchedule() {
        dayPicker.setValue(LocalDate.now());
        startTimeField.clear(); endTimeField.clear();
        activityField.clear(); responsibleField.clear(); classroomField.clear();
    }
}
