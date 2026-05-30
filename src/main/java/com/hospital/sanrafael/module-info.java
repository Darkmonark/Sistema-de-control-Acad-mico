module com.hospital.sanrafael {
requires javafx.controls;
requires javafx.fxml;
requires java.sql;
requires java.desktop;
requires java.mail;
requires java.logging;
requires org.apache.poi.ooxml;
requires kernel;
requires layout;

opens com.hospital.sanrafael to javafx.fxml;
opens com.hospital.sanrafael.controller to javafx.fxml;
exports com.hospital.sanrafael;
exports com.hospital.sanrafael.controller;
exports com.hospital.sanrafael.model;
exports com.hospital.sanrafael.service;
exports com.hospital.sanrafael.dao;
exports com.hospital.sanrafael.view;
exports com.hospital.sanrafael.database;
}
