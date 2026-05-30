package com.hospital.sanrafael.view;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public interface ViewFactory {
    Pane createMainMenuView();
    Scene createStudentScene();
    Scene createDoctorScene();
    Scene createSubjectsScene();
    Scene createScheduleScene();
    Scene createRegistryScene();
}
