package com.hospital.sanrafael.controller;

import com.hospital.sanrafael.model.DataChangeRequest;
import com.hospital.sanrafael.model.Doctor;
import com.hospital.sanrafael.model.Notification;
import com.hospital.sanrafael.model.Schedule;
import com.hospital.sanrafael.model.Student;
import com.hospital.sanrafael.service.DataChangeRequestService;
import com.hospital.sanrafael.service.DoctorService;
import com.hospital.sanrafael.service.EmailService;
import com.hospital.sanrafael.service.NotificationService;
import com.hospital.sanrafael.service.ReportService;
import com.hospital.sanrafael.service.StudentService;
import com.hospital.sanrafael.view.ViewFactory;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DoctorDashboardController extends BaseDashboardController {
    private final DoctorService doctorService;
    private final ReportService reportService;
    private final StudentService studentService;
    private final EmailService emailService;
    private final DataChangeRequestService changeRequestService;

    private TextField idField, firstNameField, lastNameField, emailField, phoneField;
    private TextField birthDateField, genderField, addressField;
    private TextField specialtyField, licenseField, areaField, experienceField;

    private TextField notificationStudentIdField;
    private TextArea notificationMessageArea;

    private ComboBox<String> reportTypeCombo;
    private ComboBox<String> reportGroupCombo;
    private DatePicker startDatePicker, endDatePicker;
    private TextArea reportResultArea;

    private ComboBox<String> emailRecipientCombo;
    private TextField emailToField;
    private TextField emailSubjectField;
    private TextArea emailBodyArea;

    public DoctorDashboardController(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
        this.doctorService = new DoctorService();
        this.reportService = ReportService.getInstance();
        this.studentService = new StudentService();
        this.emailService = EmailService.getInstance();
        this.changeRequestService = DataChangeRequestService.getInstance();
    }

    @Override
    protected String getSidebarColor() { return "#2C3E8F"; }

    @Override
    protected String getSidebarLogo() { return "MEDjestic"; }

    @Override
    protected String getSidebarLetter() { return "D"; }

    @Override
    protected String getModuleName() { return "Doctor"; }

    @Override
    protected String getModuleRole() { return "Panel del Doctor"; }

    @Override
    protected String getTitle() { 
        switch(currentSection) {
            case "profile": return "Mi Perfil";
            case "schedule": return "Mi Horario";
            case "view-notifications": return "Notificaciones";
            case "send-notification": return "Enviar Notificación";
            case "send-email": return "Enviar Correo Electrónico";
            case "reports": return "Reportes";
            case "view-students": return "Estudiantes";
            case "my-requests": return "Mis Solicitudes";
            default: return "Panel del Doctor";
        }
    }

    @Override
    protected VBox createSidebarMenuItems() {
        VBox menu = new VBox(5);
        menu.setPadding(new Insets(10, 0, 0, 0));

        boolean isProfile = currentSection.equals("profile");
        boolean isSchedule = currentSection.equals("schedule");
        boolean isSendNotif = currentSection.equals("send-notification");
        boolean isSendEmail = currentSection.equals("send-email");
        boolean isReports = currentSection.equals("reports");
        boolean isViewNotif = currentSection.equals("view-notifications");
        boolean isViewStudents = currentSection.equals("view-students");
        boolean isMyRequests = currentSection.equals("my-requests");

        Button profileBtn = sidebarBtn(" Modificar Datos", isProfile);
        Button scheduleBtn = sidebarBtn(" Mi Horario", isSchedule);
        Button viewNotifBtn = sidebarBtn(" Ver Notificaciones", isViewNotif);
        Button sendNotifBtn = sidebarBtn(" Enviar Notificacion", isSendNotif);
        Button sendEmailBtn = sidebarBtn(" Enviar Correo", isSendEmail);
        Button viewStudentsBtn = sidebarBtn(" Ver Estudiantes", isViewStudents);
        Button reportsBtn = sidebarBtn(" Reportes", isReports);
        Button myRequestsBtn = sidebarBtn(" Mis Solicitudes", isMyRequests);

        profileBtn.setOnAction(e -> { currentSection = "profile"; refreshContent(); });
        scheduleBtn.setOnAction(e -> { currentSection = "schedule"; refreshContent(); });
        viewNotifBtn.setOnAction(e -> { currentSection = "view-notifications"; refreshContent(); });
        sendNotifBtn.setOnAction(e -> { currentSection = "send-notification"; refreshContent(); });
        sendEmailBtn.setOnAction(e -> { currentSection = "send-email"; refreshContent(); });
        viewStudentsBtn.setOnAction(e -> { currentSection = "view-students"; refreshContent(); });
        reportsBtn.setOnAction(e -> { currentSection = "reports"; refreshContent(); });
        myRequestsBtn.setOnAction(e -> { currentSection = "my-requests"; refreshContent(); });

        menu.getChildren().addAll(profileBtn, scheduleBtn, viewNotifBtn, sendNotifBtn, sendEmailBtn, viewStudentsBtn, reportsBtn, myRequestsBtn);
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
            case "send-notification":
                content.getChildren().add(createSendNotificationSection());
                break;
            case "send-email":
                content.getChildren().add(createSendEmailSection());
                break;
            case "view-students":
                content.getChildren().add(createViewStudentsSection());
                break;
            case "reports":
                content.getChildren().add(createReportsSection());
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
        specialtyField = createField();
        licenseField = createField();
        areaField = createField();
        experienceField = createField();

        int r = 0;
        grid.add(fieldLabel("Nombres:"), 0, r); grid.add(firstNameField, 1, r++);
        grid.add(fieldLabel("Apellidos:"), 0, r); grid.add(lastNameField, 1, r++);
        grid.add(fieldLabel("Email:"), 0, r); grid.add(emailField, 1, r++);
        grid.add(fieldLabel("Teléfono:"), 0, r); grid.add(phoneField, 1, r++);
        grid.add(fieldLabel("Fecha Nacimiento:"), 0, r); grid.add(birthDateField, 1, r++);
        grid.add(fieldLabel("Género:"), 0, r); grid.add(genderField, 1, r++);
        grid.add(fieldLabel("Dirección:"), 0, r); grid.add(addressField, 1, r++);
        grid.add(fieldLabel("Especialidad:"), 0, r); grid.add(specialtyField, 1, r++);
        grid.add(fieldLabel("N° Colegiado:"), 0, r); grid.add(licenseField, 1, r++);
        grid.add(fieldLabel("Área:"), 0, r); grid.add(areaField, 1, r++);
        grid.add(fieldLabel("Años Experiencia:"), 0, r); grid.add(experienceField, 1, r++);

        loadCurrentDoctor();

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button saveBtn = actionBtn("Guardar Cambios", "#2C3E8F");
        Button cancelBtn = actionBtn("Cancelar", "#95A5A6");

        saveBtn.setOnAction(e -> saveChanges());
        cancelBtn.setOnAction(e -> loadCurrentDoctor());

        buttons.getChildren().addAll(saveBtn, cancelBtn);
        section.getChildren().addAll(title, grid, buttons);

        return section;
    }

    private VBox createScheduleSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label title = new Label("Mi Horario de Trabajo");
        title.setFont(Font.font("Arial Bold", 16));
        title.setStyle("-fx-text-fill: #2c3e50;");

        var currentDoctor = findCurrentDoctor();
        List<Schedule> schedules = currentDoctor != null ? currentDoctor.getCareSchedule() : List.of();

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

    private VBox createMyRequestsSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label title = new Label("Mis Solicitudes de Cambio de Datos");
        title.setFont(Font.font("Arial Bold", 16));
        title.setStyle("-fx-text-fill: #2c3e50;");

        var currentDoctor = findCurrentDoctor();
        String currentId = currentDoctor != null ? currentDoctor.getId() : null;

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

        Label title = new Label("Notificaciones Enviadas");
        title.setFont(Font.font("Arial Bold", 16));
        title.setStyle("-fx-text-fill: #2c3e50;");

        var doctor = findCurrentDoctor();
        var notifications = doctor != null
                ? notificationService.getNotificationsByPerson(doctor.getId())
                : notificationService.getAllNotifications();

        if (notifications.isEmpty()) {
            Label noNotif = new Label("No hay notificaciones registradas");
            noNotif.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            section.getChildren().addAll(title, noNotif);
        } else {
            Label info = new Label("Total de notificaciones: " + notifications.size());
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

                if (notif.getPersonName() != null) {
                    Label fromLabel = new Label("De: " + notif.getPersonName());
                    fromLabel.setFont(Font.font("Arial", 10));
                    fromLabel.setStyle("-fx-text-fill: #2C3E8F;");
                    notifBox.getChildren().add(fromLabel);
                }

                notifBox.getChildren().addAll(typeLabel, detailLabel, dateLabel);
                notifList.getChildren().add(notifBox);
            }

            section.getChildren().addAll(title, info, notifList);
        }

        return section;
    }

    private VBox createViewStudentsSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label title = new Label("Listado de Estudiantes");
        title.setFont(Font.font("Arial Bold", 16));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Consulta de estudiantes registrados en el sistema");
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setStyle("-fx-text-fill: #7f8c8d;");

        List<Student> students = studentService.getAllStudents();

        if (students.isEmpty()) {
            Label noStudents = new Label("No hay estudiantes registrados");
            noStudents.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            section.getChildren().addAll(title, subtitle, noStudents);
        } else {
            Label count = new Label("Total: " + students.size() + " estudiantes");
            count.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 13px;");

            VBox studentList = new VBox(8);
            studentList.setPadding(new Insets(10, 0, 0, 0));

            for (Student s : students) {
                VBox card = new VBox(5);
                card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-padding: 12; -fx-border-color: #e0e0e0; -fx-border-radius: 8;");

                Label nameLabel = new Label(s.getFullName());
                nameLabel.setFont(Font.font("Arial Bold", 14));
                nameLabel.setStyle("-fx-text-fill: #2c3e50;");

                Label infoLabel = new Label("ID: " + s.getId() + " | Email: " + s.getEmail() + " | Carrera: " + s.getCareer());
                infoLabel.setFont(Font.font("Arial", 12));
                infoLabel.setStyle("-fx-text-fill: #555;");

                card.getChildren().addAll(nameLabel, infoLabel);
                studentList.getChildren().add(card);
            }

            section.getChildren().addAll(title, subtitle, count, studentList);
        }

        return section;
    }

    private VBox createSendNotificationSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label title = new Label("Enviar Notificación a Estudiante");
        title.setFont(Font.font("Arial Bold", 16));
        title.setStyle("-fx-text-fill: #2c3e50;");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        notificationStudentIdField = createField();
        notificationStudentIdField.setPromptText("Ej: E001");

        notificationMessageArea = new TextArea();
        notificationMessageArea.setPrefHeight(150);
        notificationMessageArea.setPromptText("Escriba el mensaje aquí...");

        grid.add(fieldLabel("ID Estudiante:"), 0, 0);
        grid.add(notificationStudentIdField, 1, 0);
        grid.add(fieldLabel("Mensaje:"), 0, 1);
        grid.add(notificationMessageArea, 1, 1);

        Button sendBtn = actionBtn("Enviar Notificación", "#27AE60");
        sendBtn.setOnAction(e -> sendNotification());

        section.getChildren().addAll(title, grid, sendBtn);
        return section;
    }

    private VBox createSendEmailSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label title = new Label("Enviar Correo Electrónico");
        title.setFont(Font.font("Arial Bold", 16));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Notificar a estudiantes sobre información importante");
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setStyle("-fx-text-fill: #7f8c8d;");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        emailRecipientCombo = new ComboBox<>();
        emailRecipientCombo.getItems().addAll("Estudiante específico", "Todos los estudiantes", "Doctor específico", "Correo personalizado");
        emailRecipientCombo.setValue("Estudiante específico");
        emailRecipientCombo.setOnAction(e -> {
            String val = emailRecipientCombo.getValue();
            emailToField.setDisable(!val.equals("Correo personalizado") && !val.equals("Estudiante específico") && !val.equals("Doctor específico"));
            if (val.equals("Todos los estudiantes")) {
                emailToField.setText("Todos los estudiantes");
                emailToField.setDisable(true);
            }
        });

        emailToField = createField();
        emailToField.setPromptText("ID del estudiante o correo electrónico");

        emailSubjectField = createField();
        emailSubjectField.setPromptText("Asunto del correo");
        emailSubjectField.setPrefWidth(400);

        emailBodyArea = new TextArea();
        emailBodyArea.setPrefHeight(200);
        emailBodyArea.setPromptText("Escriba el mensaje aquí...");

        int r = 0;
        grid.add(fieldLabel("Destinatario:"), 0, r); grid.add(emailRecipientCombo, 1, r++);
        grid.add(fieldLabel("ID/Correo:"), 0, r); grid.add(emailToField, 1, r++);
        grid.add(fieldLabel("Asunto:"), 0, r); grid.add(emailSubjectField, 1, r++);
        grid.add(fieldLabel("Mensaje:"), 0, r); grid.add(emailBodyArea, 1, r++);

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button sendBtn = actionBtn("Enviar Correo", "#2C3E8F");
        Button clearBtn = actionBtn("Limpiar", "#95A5A6");

        sendBtn.setOnAction(e -> sendEmail());
        clearBtn.setOnAction(e -> {
            emailToField.clear();
            emailSubjectField.clear();
            emailBodyArea.clear();
            emailRecipientCombo.setValue("Estudiante específico");
        });

        buttons.getChildren().addAll(sendBtn, clearBtn);
        section.getChildren().addAll(title, subtitle, grid, buttons);
        return section;
    }

    private VBox createReportsSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label title = new Label("Sistema de Reportes - Auditoría ICONTEC");
        title.setFont(Font.font("Arial Bold", 16));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Genere reportes para procesos de acreditación. Exporte a Excel o PDF.");
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setStyle("-fx-text-fill: #7f8c8d;");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        reportTypeCombo = new ComboBox<>();
        reportTypeCombo.getItems().addAll(
            "Historial de Ingresos/Salidas",
            "Horas Cumplidas vs Requeridas",
            "Porcentaje de Cumplimiento",
            "Estudiantes Fuera de Horario",
            "Ocupación por Servicio",
            "ARL por Vencer"
        );
        reportTypeCombo.setValue("Historial de Ingresos/Salidas");
        reportTypeCombo.setOnAction(e -> {
            String type = reportTypeCombo.getValue();
            boolean needsGroup = "Porcentaje de Cumplimiento".equals(type);
            reportGroupCombo.setVisible(needsGroup);
            boolean needsDates = "Historial de Ingresos/Salidas".equals(type) || "Ocupación por Servicio".equals(type);
            startDatePicker.setVisible(needsDates);
            endDatePicker.setVisible(needsDates);
        });

        reportGroupCombo = new ComboBox<>();
        reportGroupCombo.getItems().addAll("Carrera", "Semestre", "Turno", "Todos");
        reportGroupCombo.setValue("Carrera");
        reportGroupCombo.setVisible(false);

        startDatePicker = new DatePicker(LocalDate.now().minusMonths(1));
        endDatePicker = new DatePicker(LocalDate.now());

        int r = 0;
        grid.add(fieldLabel("Tipo de Reporte:"), 0, r); grid.add(reportTypeCombo, 1, r++);
        grid.add(fieldLabel("Agrupar por:"), 0, r); grid.add(reportGroupCombo, 1, r++);
        grid.add(fieldLabel("Fecha Inicio:"), 0, r); grid.add(startDatePicker, 1, r++);
        grid.add(fieldLabel("Fecha Fin:"), 0, r); grid.add(endDatePicker, 1, r++);

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button generateBtn = actionBtn("Generar Reporte", "#8E44AD");
        Button exportExcelBtn = actionBtn("Exportar a Excel", "#27AE60");
        Button exportPdfBtn = actionBtn("Exportar a PDF", "#E74C3C");

        generateBtn.setOnAction(e -> generateReport());
        exportExcelBtn.setOnAction(e -> exportToExcel());
        exportPdfBtn.setOnAction(e -> exportToPdf());

        buttons.getChildren().addAll(generateBtn, exportExcelBtn, exportPdfBtn);

        reportResultArea = new TextArea();
        reportResultArea.setPrefHeight(350);
        reportResultArea.setEditable(false);
        reportResultArea.setFont(Font.font("Consolas", 12));
        reportResultArea.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ddd; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10;");
        reportResultArea.setText("Seleccione un tipo de reporte y presione 'Generar Reporte'");

        section.getChildren().addAll(title, subtitle, grid, buttons, reportResultArea);
        return section;
    }

    @Override
    protected int getFilteredNotificationCount() {
        var doctor = findCurrentDoctor();
        if (doctor != null) {
            return notificationService.getUnreadCountForPerson(doctor.getId());
        }
        return super.getFilteredNotificationCount();
    }

    private Doctor findCurrentDoctor() {
        var user = mainController != null ? mainController.getCurrentUser() : null;
        if (user == null) return null;
        String userEmail = user.getEmail();
        String userName = user.getFullName();
        var doctors = doctorService.getAllDoctors();
        for (var d : doctors) {
            if (userEmail.equalsIgnoreCase(d.getEmail())) return d;
        }
        for (var d : doctors) {
            if (userName.equalsIgnoreCase(d.getFullName())) return d;
        }
        return null;
    }

    private void loadCurrentDoctor() {
        var currentDoctor = findCurrentDoctor();

        if (currentDoctor != null) {
            idField.setText(currentDoctor.getId());
            firstNameField.setText(currentDoctor.getFirstName());
            lastNameField.setText(currentDoctor.getLastName());
            emailField.setText(currentDoctor.getEmail());
            phoneField.setText(currentDoctor.getPhone());
            birthDateField.setText(currentDoctor.getBirthDate());
            genderField.setText(currentDoctor.getGender());
            addressField.setText(currentDoctor.getAddress());
            specialtyField.setText(currentDoctor.getSpecialty());
            licenseField.setText(currentDoctor.getLicenseNumber());
            areaField.setText(currentDoctor.getAssignedArea());
            experienceField.setText(String.valueOf(currentDoctor.getYearsExperience()));
        }
    }

    private void saveChanges() {
        var currentDoctor = findCurrentDoctor();

        if (currentDoctor == null) {
            show("Error", "No se encontr\u00F3 un registro de doctor vinculado a tu cuenta. Contacta al administrador.");
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
            changes.put("Especialidad", specialtyField.getText().trim());
            changes.put("N Colegiado", licenseField.getText().trim());
            changes.put("Area", areaField.getText().trim());
            changes.put("Anios Experiencia", experienceField.getText().trim());

            changeRequestService.submitDoctorChange(currentDoctor, changes);
            show("Solicitud Enviada",
                "Se ha enviado una solicitud de cambio de datos al administrador.\n" +
                "Recibir\u00E1s una notificaci\u00F3n cuando sea aprobada o denegada.");
        } catch (Exception e) {
            show("Error", "No se pudo enviar la solicitud: " + e.getMessage());
        }
    }

    private void sendNotification() {
        String studentId = notificationStudentIdField.getText().trim();
        String message = notificationMessageArea.getText().trim();

        if (studentId.isEmpty() || message.isEmpty()) {
            show("Error", "Complete todos los campos");
            return;
        }

        notificationService.addNotification(
            "Notificación del Doctor: " + message,
            "ID: " + studentId,
            Notification.Type.INFO,
            Notification.Category.GENERAL,
            studentId,
            "Doctor"
        );

        show("Éxito", "Notificación enviada al estudiante " + studentId);
        notificationStudentIdField.clear();
        notificationMessageArea.clear();
    }

    private void sendEmail() {
        String recipientType = emailRecipientCombo.getValue();
        String to = emailToField.getText().trim();
        String subject = emailSubjectField.getText().trim();
        String body = emailBodyArea.getText().trim();

        if ("Todos los estudiantes".equals(recipientType)) {
            List<Student> students = studentService.getAllStudents();
            if (students.isEmpty()) {
                show("Error", "No hay estudiantes registrados");
                return;
            }
            int sent = 0;
            int failed = 0;
            for (Student s : students) {
                try {
                    emailService.sendEmail(s.getEmail(), subject, body);
                    sent++;
                } catch (Exception ex) {
                    failed++;
                }
            }
            show("Éxito", "Correos enviados: " + sent + " exitosos, " + failed + " fallidos");
            return;
        }

        if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            show("Error", "Complete todos los campos del correo");
            return;
        }

        String emailAddress = to;
        if ("Estudiante específico".equals(recipientType)) {
            Student student = studentService.getStudentById(to);
            if (student != null) {
                emailAddress = student.getEmail();
            } else {
                show("Error", "Estudiante con ID " + to + " no encontrado");
                return;
            }
        } else if ("Doctor específico".equals(recipientType)) {
            Doctor doctor = doctorService.getDoctorById(to);
            if (doctor != null) {
                emailAddress = doctor.getEmail();
            } else {
                show("Error", "Doctor con ID " + to + " no encontrado");
                return;
            }
        }

        try {
            emailService.sendEmail(emailAddress, subject, body, false);
            show("Éxito", "Correo enviado exitosamente a " + emailAddress);
            emailToField.clear();
            emailSubjectField.clear();
            emailBodyArea.clear();
        } catch (Exception ex) {
            show("Error", "No se pudo enviar el correo: " + ex.getMessage());
        }
    }

    private void generateReport() {
        try {
            String reportType = reportTypeCombo.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String result;

            if (startDate == null) startDate = LocalDate.now().minusMonths(1);
            if (endDate == null) endDate = LocalDate.now();

            switch (reportType) {
                case "Historial de Ingresos/Salidas":
                    result = reportService.generateEntryExitHistory(startDate, endDate);
                    break;
                case "Horas Cumplidas vs Requeridas":
                    result = reportService.generateHoursComplianceReport();
                    break;
                case "Porcentaje de Cumplimiento":
                    String group = reportGroupCombo.getValue();
                    result = reportService.generateCompliancePercentageByGroup(group != null ? group : "Todos");
                    break;
                case "Estudiantes Fuera de Horario":
                    result = reportService.generateShiftViolationsReport();
                    break;
                case "Ocupación por Servicio":
                    result = reportService.generateHistoricalOccupancy();
                    break;
                case "ARL por Vencer":
                    result = reportService.generateArlExpiringReport();
                    break;
                default:
                    result = "Seleccione un tipo de reporte válido";
            }

            reportResultArea.setText(result);
        } catch (Exception e) {
            reportResultArea.setText("Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void exportToExcel() {
        String reportType = reportTypeCombo.getValue();
        String filename = "reporte_" + reportType.replaceAll("\\s+", "_") + "_" + LocalDate.now() + ".xlsx";
        String content = reportResultArea.getText();
        reportService.exportToExcel(content, reportType, filename);
        show("Éxito", "Reporte exportado a: " + filename);
    }

    private void exportToPdf() {
        String reportType = reportTypeCombo.getValue();
        String filename = "reporte_" + reportType.replaceAll("\\s+", "_") + "_" + LocalDate.now() + ".pdf";
        String content = reportResultArea.getText();
        reportService.exportToPdf(content, reportType, filename);
        show("Éxito", "Reporte PDF exportado a: " + filename);
    }
}
