package com.hospital.sanrafael.service;

import com.hospital.sanrafael.dao.DoctorDAO;
import com.hospital.sanrafael.dao.PostgreDoctorDAO;
import com.hospital.sanrafael.database.DatabaseConnection;
import com.hospital.sanrafael.model.Doctor;
import com.hospital.sanrafael.model.Schedule;

import java.util.List;

public class DoctorService {
    private final DoctorDAO fileDAO;
    private final PostgreDoctorDAO dbDAO;
    private final boolean useDatabase;

    public DoctorService() {
        this.fileDAO = new DoctorDAO();
        this.dbDAO = new PostgreDoctorDAO();
        this.useDatabase = DatabaseConnection.testConnection();
    }

    public List<Doctor> getAllDoctors() {
        return useDatabase ? dbDAO.getAll() : fileDAO.getAll();
    }

    public Doctor getDoctorById(String id) {
        return useDatabase ? dbDAO.getById(id) : fileDAO.getById(id);
    }

    public Doctor getDoctorByLicenseNumber(String licenseNumber) {
        return useDatabase ? dbDAO.getByLicenseNumber(licenseNumber) : fileDAO.getByLicenseNumber(licenseNumber);
    }

    public Doctor registerDoctor(Doctor doctor) {
        doctor.setId(generateNextDoctorId());
        if (useDatabase) {
            dbDAO.save(doctor);
        } else {
            fileDAO.save(doctor);
        }
        return doctor;
    }

    private String generateNextDoctorId() {
        List<Doctor> all = getAllDoctors();
        int max = 0;
        for (Doctor d : all) {
            String id = d.getId();
            if (id != null && id.startsWith("D")) {
                try {
                    int n = Integer.parseInt(id.substring(1));
                    if (n > max) max = n;
                } catch (NumberFormatException e) {}
            }
        }
        return "D" + String.format("%03d", max + 1);
    }

    public Doctor updateDoctor(Doctor doctor) {
        if (useDatabase) {
            dbDAO.update(doctor);
        } else {
            fileDAO.update(doctor);
        }
        return doctor;
    }

    public void deleteDoctor(String id) {
        if (useDatabase) {
            dbDAO.delete(id);
        } else {
            fileDAO.delete(id);
        }
    }

    public List<Doctor> searchBySpecialty(String specialty) {
        return useDatabase ? dbDAO.getBySpecialty(specialty) : fileDAO.getBySpecialty(specialty);
    }

    public void assignStudentToDoctor(String doctorId, String studentId) {
        Doctor doctor = getDoctorById(doctorId);
        if (doctor != null) {
            doctor.addAssignedStudent(studentId);
            updateDoctor(doctor);
        }
    }

    public void removeStudentFromDoctor(String doctorId, String studentId) {
        Doctor doctor = getDoctorById(doctorId);
        if (doctor != null) {
            doctor.removeAssignedStudent(studentId);
            updateDoctor(doctor);
        }
    }

    public void addCareSchedule(Doctor doctor, Schedule schedule) {
        doctor.addCareSchedule(schedule);
        updateDoctor(doctor);
    }

    public void removeCareSchedule(Doctor doctor, Schedule schedule) {
        doctor.removeCareSchedule(schedule);
        updateDoctor(doctor);
    }

    public List<Schedule> getCareSchedule(String doctorId) {
        Doctor doctor = getDoctorById(doctorId);
        if (doctor != null) {
            return doctor.getCareSchedule();
        }
        return List.of();
    }
}
