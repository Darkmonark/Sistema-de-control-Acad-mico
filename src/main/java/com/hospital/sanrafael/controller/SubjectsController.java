package com.hospital.sanrafael.controller;

import com.hospital.sanrafael.model.Subject;
import com.hospital.sanrafael.service.SubjectService;
import com.hospital.sanrafael.view.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class SubjectsController extends BaseDashboardController {
    private final SubjectService subjectService;
    private TableView<Subject> tableView;
    private TextField codeField, nameField, descriptionField;
    private TextField creditsField, semesterField, professorField, classroomField;

    public SubjectsController(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
        this.subjectService = new SubjectService();
    }

    @Override protected String getSidebarColor() { return "#8E44AD"; }
    @Override protected String getSidebarLogo() { return "MEDjestic"; }
    @Override protected String getSidebarLetter() { return "M"; }
    @Override protected String getModuleName() { return "Materias"; }
    @Override protected String getModuleRole() { return "M\u00F3dulo Acad\u00E9mico"; }
    @Override protected String getTitle() {
        if ("view-notifications".equals(currentSection)) return "Notificaciones";
        return "Gesti\u00F3n de Materias";
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

        HBox stats = new HBox(15);
        int total = subjectService.getAllSubjects().size();
        stats.getChildren().addAll(
            statCard("TOTAL MATERIAS", String.valueOf(total), "#8E44AD"),
            statCard("PROFESORES", "", "#27AE60"),
            statCard("SEMESTRES", "10", "#E67E22"),
            statCard("CR\u00C9DITOS PROM.", "", "#E74C3C")
        );

        VBox tableSection = new VBox(10);
        tableSection.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label sectionTitle = new Label("Lista de Materias");
        sectionTitle.setFont(Font.font("Arial Bold", 16));
        sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

        tableView = new TableView<>();
        tableView.setItems(getSubjectsData());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefHeight(180);
        createColumns();

        VBox formSection = new VBox(10);
        formSection.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-padding: 15;");

        Label formTitle = new Label("Formulario de Materias");
        formTitle.setFont(Font.font("Arial Bold", 14));
        formTitle.setStyle("-fx-text-fill: #2c3e50;");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        codeField = createField();
        nameField = createField();
        descriptionField = createField();
        creditsField = createField();
        semesterField = createField();
        professorField = createField();
        classroomField = createField();

        int r = 0;
        grid.add(fieldLabel("Nombre:"), 0, r); grid.add(nameField, 1, r);
        grid.add(fieldLabel("Descripci\u00F3n:"), 2, r); grid.add(descriptionField, 3, r++);
        grid.add(fieldLabel("Cr\u00E9ditos:"), 0, r); grid.add(creditsField, 1, r);
        grid.add(fieldLabel("Semestre:"), 2, r); grid.add(semesterField, 3, r++);
        grid.add(fieldLabel("Profesor:"), 0, r); grid.add(professorField, 1, r);
        grid.add(fieldLabel("Aula:"), 2, r); grid.add(classroomField, 3, r++);

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

    private void createColumns() {
        TableColumn<Subject, String> c1 = new TableColumn<>("C\u00F3digo");
        c1.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCode()));
        TableColumn<Subject, String> c2 = new TableColumn<>("Nombre");
        c2.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getName()));
        TableColumn<Subject, String> c3 = new TableColumn<>("Profesor");
        c3.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getProfessor()));
        TableColumn<Subject, Number> c4 = new TableColumn<>("Cr\u00E9ditos");
        c4.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getCredits()));
        TableColumn<Subject, Number> c5 = new TableColumn<>("Semestre");
        c5.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getRecommendedSemester()));
        tableView.getColumns().addAll(c1, c2, c3, c4, c5);
    }

    private void fillForm(Subject s) {
        codeField.setText(s.getCode());
        nameField.setText(s.getName());
        descriptionField.setText(s.getDescription());
        creditsField.setText(String.valueOf(s.getCredits()));
        semesterField.setText(String.valueOf(s.getRecommendedSemester()));
        professorField.setText(s.getProfessor());
        classroomField.setText(s.getClassroom());
    }

    private void clearForm() {
        codeField.clear(); nameField.clear(); descriptionField.clear();
        creditsField.clear(); semesterField.clear(); professorField.clear(); classroomField.clear();
    }

    private void save() {
        try {
            if (fieldsEmpty()) {
                show("Error", "Complete todos los campos obligatorios");
                return;
            }
            Subject s = new Subject(null, nameField.getText(), descriptionField.getText(),
                parseInt(creditsField.getText(), 1), parseInt(semesterField.getText(), 1),
                professorField.getText(), classroomField.getText());
            subjectService.registerSubject(s);
            tableView.setItems(getSubjectsData());
            clearForm();
            show("\u00C9xito", "Materia registrada");
        } catch (Exception ex) {
            show("Error", "Error: " + ex.getMessage());
        }
    }

    private void update() {
        try {
            if (codeField.getText() == null || codeField.getText().isEmpty()) {
                show("Error", "Seleccione una materia de la tabla");
                return;
            }
            Subject s = new Subject(codeField.getText(), nameField.getText(), descriptionField.getText(),
                parseInt(creditsField.getText(), 1), parseInt(semesterField.getText(), 1),
                professorField.getText(), classroomField.getText());
            subjectService.updateSubject(s);
            tableView.setItems(getSubjectsData());
            show("\u00C9xito", "Materia actualizada");
        } catch (Exception ex) {
            show("Error", "Error: " + ex.getMessage());
        }
    }

    private void delete() {
        try {
            String code = codeField.getText();
            if (code == null || code.isEmpty()) {
                show("Error", "Seleccione una materia de la tabla");
                return;
            }
            subjectService.deleteSubject(code);
            tableView.setItems(getSubjectsData());
            clearForm();
            show("\u00C9xito", "Materia eliminada");
        } catch (Exception ex) {
            show("Error", "Error: " + ex.getMessage());
        }
    }

    private boolean fieldsEmpty() {
        return nameField.getText().trim().isEmpty()
            || professorField.getText().trim().isEmpty()
            || creditsField.getText().trim().isEmpty()
            || semesterField.getText().trim().isEmpty();
    }

    private int parseInt(String val, int def) {
        if (val == null || val.trim().isEmpty()) return def;
        try { return Integer.parseInt(val.trim()); } catch (NumberFormatException e) { return def; }
    }

    private ObservableList<Subject> getSubjectsData() {
        return FXCollections.observableArrayList(subjectService.getAllSubjects());
    }
}
