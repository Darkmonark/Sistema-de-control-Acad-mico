package com.hospital.sanrafael.controller;

import com.hospital.sanrafael.model.DataChangeRequest;
import com.hospital.sanrafael.model.Notification;
import com.hospital.sanrafael.model.Schedule;
import com.hospital.sanrafael.model.Student;
import com.hospital.sanrafael.service.DataChangeRequestService;
import com.hospital.sanrafael.service.NotificationService;
import com.hospital.sanrafael.service.StudentService;
import com.hospital.sanrafael.view.ViewFactory;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentDashboardController extends BaseDashboardController {
private final StudentService studentService;
private final NotificationService notificationService;
private final DataChangeRequestService changeRequestService;

// Campos del formulario
private TextField idField, firstNameField, lastNameField, emailField, phoneField;
private TextField birthDateField, genderField, addressField, careerField, semesterField, arlDateField;

public StudentDashboardController(ViewFactory viewFactory) {
this.viewFactory = viewFactory;
this.studentService = new StudentService();
this.notificationService = NotificationService.getInstance();
this.changeRequestService = DataChangeRequestService.getInstance();
}

    @Override
    protected String getSidebarColor() { return "#27AE60"; }
    
    @Override
    protected String getSidebarLogo() { return "MEDjestic"; }
    
    @Override
    protected String getSidebarLetter() { return "E"; }
    
    @Override
    protected String getModuleName() { return "Estudiante"; }
    
    @Override
    protected String getModuleRole() { return "Panel del Estudiante"; }
    
    @Override
    protected String getTitle() { 
        switch(currentSection) {
            case "profile": return "Modificar Datos";
            case "schedule": return "Mi Horario";
            case "view-notifications": return "Notificaciones";
            case "my-requests": return "Mis Solicitudes";
            default: return "Panel del Estudiante";
        }
    }

    @Override
    protected VBox createSidebarMenuItems() {
        VBox menu = new VBox(5);
        menu.setPadding(new Insets(10, 0, 0, 0));
        
        boolean isProfile = currentSection.equals("profile");
        boolean isSchedule = currentSection.equals("schedule");
        boolean isViewNotif = currentSection.equals("view-notifications");
        boolean isMyRequests = currentSection.equals("my-requests");
        
        Button profileBtn = sidebarBtn(" Modificar Datos", isProfile);
        Button scheduleBtn = sidebarBtn(" Mi Horario", isSchedule);
        Button viewNotifBtn = sidebarBtn(" Ver Notificaciones", isViewNotif);
        Button myRequestsBtn = sidebarBtn(" Mis Solicitudes", isMyRequests);
        
        profileBtn.setOnAction(e -> { currentSection = "profile"; refreshContent(); });
        scheduleBtn.setOnAction(e -> { currentSection = "schedule"; refreshContent(); });
        viewNotifBtn.setOnAction(e -> { currentSection = "view-notifications"; refreshContent(); });
        myRequestsBtn.setOnAction(e -> { currentSection = "my-requests"; refreshContent(); });
        
        menu.getChildren().addAll(profileBtn, scheduleBtn, viewNotifBtn, myRequestsBtn);
        return menu;
    }

    @Override
    protected VBox createContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: #f0f4f8;");
        
        switch(currentSection) {
            case "profile":
                content.getChildren().add(createProfileSection());
                break;
            case "schedule":
                content.getChildren().add(createScheduleSection());
                break;
            case "view-notifications":
                content.getChildren().add(createViewNotificationsSection());
                break;
            case "my-requests":
                content.getChildren().add(createMyRequestsSection());
                break;
            default:
                content.getChildren().add(createProfileSection());
        }
        
        return content;
    }
    
    private VBox createProfileSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");
        
        Label title = new Label("Modificar Datos Personales");
        title.setFont(Font.font("Arial Bold", 16));
        title.setStyle("-fx-text-fill: #2c3e50;");
        
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
        arlDateField = createField();
        
        int r = 0;
        grid.add(fieldLabel("Nombres:"), 0, r); grid.add(firstNameField, 1, r++);
        grid.add(fieldLabel("Apellidos:"), 0, r); grid.add(lastNameField, 1, r++);
        grid.add(fieldLabel("Email:"), 0, r); grid.add(emailField, 1, r++);
        grid.add(fieldLabel("Teléfono:"), 0, r); grid.add(phoneField, 1, r++);
        grid.add(fieldLabel("Fecha Nacimiento:"), 0, r); grid.add(birthDateField, 1, r++);
        grid.add(fieldLabel("Género:"), 0, r); grid.add(genderField, 1, r++);
        grid.add(fieldLabel("Dirección:"), 0, r); grid.add(addressField, 1, r++);
        grid.add(fieldLabel("Carrera:"), 0, r); grid.add(careerField, 1, r++);
        grid.add(fieldLabel("Semestre:"), 0, r); grid.add(semesterField, 1, r++);
        grid.add(fieldLabel("Vencimiento ARL:"), 0, r); grid.add(arlDateField, 1, r++);
        
        loadCurrentStudent();
        
        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));
        
        Button saveBtn = actionBtn("Guardar Cambios", "#27AE60");
        Button cancelBtn = actionBtn("Cancelar", "#95A5A6");
        
        saveBtn.setOnAction(e -> saveChanges());
        cancelBtn.setOnAction(e -> loadCurrentStudent());
        
        buttons.getChildren().addAll(saveBtn, cancelBtn);
        section.getChildren().addAll(title, grid, buttons);
        
        return section;
    }
    
    private VBox createScheduleSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label title = new Label("Mi Horario de Clases");
        title.setFont(Font.font("Arial Bold", 16));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Student currentStudent = findCurrentStudent();
        List<Schedule> schedules = currentStudent != null ? currentStudent.getWeeklySchedule() : List.of();

        if (schedules.isEmpty()) {
            Label empty = new Label("No tienes horarios asignados. Consulta con el administrador.");
            empty.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            section.getChildren().addAll(title, empty);
            return section;
        }

        TableView<Schedule> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);

        TableColumn<Schedule, String> colDay = new TableColumn<>("D\u00EDa");
        colDay.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDay()));
        TableColumn<Schedule, String> colStart = new TableColumn<>("Inicio");
        colStart.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStartTime()));
        TableColumn<Schedule, String> colEnd = new TableColumn<>("Fin");
        colEnd.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getEndTime()));
        TableColumn<Schedule, String> colActivity = new TableColumn<>("Actividad");
        colActivity.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getActivity()));
        TableColumn<Schedule, String> colResp = new TableColumn<>("Responsable");
        colResp.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getResponsible()));
        TableColumn<Schedule, String> colRoom = new TableColumn<>("Aula");
        colRoom.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getClassroom()));

        table.getColumns().addAll(colDay, colStart, colEnd, colActivity, colResp, colRoom);
        table.setItems(FXCollections.observableArrayList(schedules));

        section.getChildren().addAll(title, table);
        return section;
    }
    
    @Override
    protected int getFilteredNotificationCount() {
        var student = findCurrentStudent();
        if (student != null) {
            return notificationService.getUnreadCountForPerson(student.getId());
        }
        return super.getFilteredNotificationCount();
    }

    private Student findCurrentStudent() {
        var user = mainController != null ? mainController.getCurrentUser() : null;
        if (user == null) return null;
        String userEmail = user.getEmail();
        String userName = user.getFullName();
        var students = studentService.getAllStudents();
        for (var s : students) {
            if (userEmail.equalsIgnoreCase(s.getEmail())) return s;
        }
        for (var s : students) {
            if (userName.equalsIgnoreCase(s.getFullName())) return s;
        }
        return null;
    }

    private void loadCurrentStudent() {
        var currentStudent = findCurrentStudent();
        
        if (currentStudent != null) {
            idField.setText(currentStudent.getId());
            firstNameField.setText(currentStudent.getFirstName());
            lastNameField.setText(currentStudent.getLastName());
            emailField.setText(currentStudent.getEmail());
            phoneField.setText(currentStudent.getPhone());
            birthDateField.setText(currentStudent.getBirthDate());
            genderField.setText(currentStudent.getGender());
            addressField.setText(currentStudent.getAddress());
            careerField.setText(currentStudent.getCareer());
            semesterField.setText(String.valueOf(currentStudent.getSemester()));
            arlDateField.setText(currentStudent.getArlExpirationDate() != null ? 
                currentStudent.getArlExpirationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "");
        }
    }
    
    private void saveChanges() {
        var currentStudent = findCurrentStudent();
        
        if (currentStudent == null) {
            show("Error", "No se encontr\u00F3 un registro de estudiante vinculado a tu cuenta. Contacta al administrador.");
            return;
        }
        
        try {
            Map<String, String> changes = new LinkedHashMap<>();
            changes.put("Nombres", firstNameField.getText().trim());
            changes.put("Apellidos", lastNameField.getText().trim());
            changes.put("Email", emailField.getText().trim());
            changes.put("Telefono", phoneField.getText().trim());
            changes.put("Fecha Nacimiento", birthDateField.getText().trim());
            changes.put("Genero", genderField.getText().trim());
            changes.put("Direccion", addressField.getText().trim());
                changes.put("Carrera", careerField.getText().trim());
                changes.put("Semestre", semesterField.getText().trim());

                changeRequestService.submitStudentChange(currentStudent, changes);
            show("Solicitud Enviada", 
                "Se ha enviado una solicitud de cambio de datos al administrador.\n" +
                "Recibir\u00E1s una notificaci\u00F3n cuando sea aprobada o denegada.");
        } catch (Exception e) {
            show("Error", "No se pudo enviar la solicitud: " + e.getMessage());
        }
    }
    
    private VBox createMyRequestsSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label title = new Label("Mis Solicitudes de Cambio de Datos");
        title.setFont(Font.font("Arial Bold", 16));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Student currentStudent = findCurrentStudent();
        String currentId = currentStudent != null ? currentStudent.getId() : null;

        List<DataChangeRequest> myRequests = currentId != null ?
            changeRequestService.getRequestsByRequester(currentId) : List.of();

        if (myRequests.isEmpty()) {
            Label noRequests = new Label("No has realizado ninguna solicitud de cambio de datos");
            noRequests.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            section.getChildren().addAll(title, noRequests);
        } else {
            Label info = new Label("Tus solicitudes: " + myRequests.size());
            info.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

            VBox requestList = new VBox(10);
            requestList.setPadding(new Insets(10, 0, 0, 0));

            for (var req : myRequests) {
                VBox reqBox = new VBox(5);
                String statusColor;
                String statusText;
                switch (req.getStatus()) {
                    case APPROVED:
                        statusColor = "#27AE60";
                        statusText = "APROBADA";
                        break;
                    case DENIED:
                        statusColor = "#E74C3C";
                        statusText = "DENEGADA";
                        break;
                    default:
                        statusColor = "#F39C12";
                        statusText = "PENDIENTE";
                }
                reqBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-padding: 12; -fx-border-color: #ddd; -fx-border-radius: 8;");

                Label statusLabel = new Label("[" + statusText + "] Solicitud del " + req.getRequestDate());
                statusLabel.setFont(Font.font("Arial Bold", 12));
                statusLabel.setStyle("-fx-text-fill: " + statusColor + ";");

                Label fieldsLabel = new Label("Campos a modificar: " + String.join(", ", req.getProposedData().keySet()));
                fieldsLabel.setFont(Font.font("Arial", 11));
                fieldsLabel.setStyle("-fx-text-fill: #555;");

                reqBox.getChildren().addAll(statusLabel, fieldsLabel);

                if (req.getAdminMessage() != null && !req.getAdminMessage().isEmpty()) {
                    Label msgLabel = new Label("Mensaje del admin: " + req.getAdminMessage());
                    msgLabel.setFont(Font.font("Arial", 11));
                    msgLabel.setStyle("-fx-text-fill: #2C3E8F; -fx-font-style: italic;");
                    reqBox.getChildren().add(msgLabel);
                }

                requestList.getChildren().add(reqBox);
            }

            section.getChildren().addAll(title, info, requestList);
        }

        return section;
    }

    private VBox createViewNotificationsSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");
        
        Label title = new Label("Bandeja de Notificaciones");
        title.setFont(Font.font("Arial Bold", 16));
        title.setStyle("-fx-text-fill: #2c3e50;");
        
        Student currentStudent = findCurrentStudent();
        String currentStudentId = currentStudent != null ? currentStudent.getId() : null;
        
        var allNotifications = notificationService.getAllNotifications();
        var notifications = allNotifications.stream()
                .filter(n -> currentStudentId == null || currentStudentId.equals(n.getPersonId()) || n.getPersonId() == null)
                .toList();
        
        if (notifications.isEmpty()) {
            Label noNotif = new Label("No hay notificaciones para ti");
            noNotif.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            section.getChildren().addAll(title, noNotif);
        } else {
            Label info = new Label("Tus notificaciones: " + notifications.size());
            info.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
            
            VBox notifList = new VBox(10);
            notifList.setPadding(new Insets(10, 0, 0, 0));
            
            for (var notif : notifications) {
                VBox notifBox = new VBox(5);
                notifBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-padding: 12; -fx-border-color: #ddd; -fx-border-radius: 8;");
                
                Label typeLabel = new Label("[" + notif.getType() + "] " + notif.getMessage());
                typeLabel.setFont(Font.font("Arial Bold", 12));
                typeLabel.setStyle("-fx-text-fill: #2c3e50;");
                
                Label detailLabel = new Label(notif.getDetails() != null ? notif.getDetails() : "");
                detailLabel.setFont(Font.font("Arial", 11));
                detailLabel.setStyle("-fx-text-fill: #555;");
                
                Label dateLabel = new Label("Fecha: " + notif.getDate());
                dateLabel.setFont(Font.font("Arial", 10));
                dateLabel.setStyle("-fx-text-fill: #999;");
                
                notifBox.getChildren().addAll(typeLabel, detailLabel, dateLabel);
                notifList.getChildren().add(notifBox);
            }
            
            section.getChildren().addAll(title, info, notifList);
        }
        
        return section;
    }
}
