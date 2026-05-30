package com.hospital.sanrafael.controller;

import com.hospital.sanrafael.model.User;
import com.hospital.sanrafael.view.ViewFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {
    private final ViewFactory viewFactory;
    private Stage primaryStage;
    private User currentUser;
    private String doctorMenuMode = "menu";

    public MainController(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }

    public void initialize(Stage stage) {
        this.primaryStage = stage;

        LoginController loginController = new LoginController(viewFactory);
        loginController.setMainController(this);

        Scene scene = new Scene(loginController.getView(), 1400, 750);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hospital San Rafael - Management System");
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }
    public String getDoctorMenuMode() { return doctorMenuMode; }
    public void setDoctorMenuMode(String mode) { this.doctorMenuMode = mode; }

    public void navigateTo(String view) {
        Scene scene = null;

        try {
            switch (view) {
                case "login":
                    LoginController loginController = new LoginController(viewFactory);
                    loginController.setMainController(this);
                    scene = new Scene(loginController.getView(), 1400, 750);
                    break;
                case "register":
                    RegisterController registerController = new RegisterController(viewFactory);
                    registerController.setMainController(this);
                    scene = new Scene(registerController.getView(), 1400, 750);
                    break;
                case "main":
                    MainMenuController mainMenuController = new MainMenuController(viewFactory);
                    mainMenuController.setMainController(this);
                    scene = new Scene(mainMenuController.getView(), 1400, 750);
                    break;
                case "students":
                    StudentController studentController = new StudentController(viewFactory);
                    studentController.setMainController(this);
                    scene = new Scene(studentController.getView(), 1400, 750);
                    break;
                case "doctors":
                    DoctorController doctorController = new DoctorController(viewFactory);
                    doctorController.setMainController(this);
                    scene = new Scene(doctorController.getView(), 1400, 750);
                    break;
                case "records":
                    RegistryController registryController = new RegistryController(viewFactory);
                    registryController.setMainController(this);
                    scene = new Scene(registryController.getView(), 1400, 750);
                    break;
                case "subjects":
                    SubjectsController subjectsController = new SubjectsController(viewFactory);
                    subjectsController.setMainController(this);
                    scene = new Scene(subjectsController.getView(), 1400, 750);
                    break;
                case "schedules":
                    ScheduleController scheduleController = new ScheduleController(viewFactory);
                    scheduleController.setMainController(this);
                    scene = new Scene(scheduleController.getView(), 1400, 750);
                    break;
case "doctor-menu":
DoctorMenuController doctorMenuController = new DoctorMenuController(viewFactory);
doctorMenuController.setMainController(this);
scene = new Scene(doctorMenuController.getView(), 1400, 750);
break;
case "student-dashboard":
StudentDashboardController studentDashController = new StudentDashboardController(viewFactory);
studentDashController.setMainController(this);
scene = new Scene(studentDashController.getView(), 1400, 750);
break;
case "doctor-dashboard":
DoctorDashboardController doctorDashController = new DoctorDashboardController(viewFactory);
doctorDashController.setMainController(this);
scene = new Scene(doctorDashController.getView(), 1400, 750);
break;
case "change-requests":
StudentController requestsController = new StudentController(viewFactory);
requestsController.setMainController(this);
requestsController.setCurrentSection("requests");
scene = new Scene(requestsController.getView(), 1400, 750);
break;
default:
MainMenuController mmc = new MainMenuController(viewFactory);
mmc.setMainController(this);
scene = new Scene(mmc.getView(), 1400, 750);
            }

            if (scene != null) {
                primaryStage.setScene(scene);
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error navigating to: " + view);
        }
    }
}
