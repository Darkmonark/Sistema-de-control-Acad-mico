package com.hospital.sanrafael.view;

import com.hospital.sanrafael.controller.*;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class FXViewFactory implements ViewFactory {

    @Override
    public Pane createMainMenuView() {
        MainMenuController controller = new MainMenuController(this);
        return controller.getView();
    }

    @Override
    public Scene createStudentScene() {
        StudentController controller = new StudentController(this);
        return new Scene(controller.getView(), 900, 600);
    }

    @Override
    public Scene createDoctorScene() {
        DoctorController controller = new DoctorController(this);
        return new Scene(controller.getView(), 900, 600);
    }

    @Override
    public Scene createSubjectsScene() {
        SubjectsController controller = new SubjectsController(this);
        return new Scene(controller.getView(), 900, 600);
    }

    @Override
    public Scene createScheduleScene() {
        ScheduleController controller = new ScheduleController(this);
        return new Scene(controller.getView(), 900, 600);
    }

    @Override
    public Scene createRegistryScene() {
        RegistryController controller = new RegistryController(this);
        return new Scene(controller.getView(), 900, 600);
    }
}
