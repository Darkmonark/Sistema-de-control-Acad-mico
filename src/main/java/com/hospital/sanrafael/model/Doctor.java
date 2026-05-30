package com.hospital.sanrafael.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Doctor extends Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String specialty;
    private String licenseNumber;
    private String assignedArea;
    private int yearsExperience;
    private List<String> assignedStudents;
    private List<Schedule> careSchedule;

    public Doctor() {
        this.assignedStudents = new ArrayList<>();
        this.careSchedule = new ArrayList<>();
    }

    public Doctor(String id, String firstName, String lastName, String email, String phone,
                  String birthDate, String gender, String address,
                  String specialty, String licenseNumber, String assignedArea, int yearsExperience) {
        super(id, firstName, lastName, email, phone, birthDate, gender, address);
        this.specialty = specialty;
        this.licenseNumber = licenseNumber;
        this.assignedArea = assignedArea;
        this.yearsExperience = yearsExperience;
        this.assignedStudents = new ArrayList<>();
        this.careSchedule = new ArrayList<>();
    }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public String getAssignedArea() { return assignedArea; }
    public void setAssignedArea(String assignedArea) { this.assignedArea = assignedArea; }
    public int getYearsExperience() { return yearsExperience; }
    public void setYearsExperience(int yearsExperience) { this.yearsExperience = yearsExperience; }

    public List<String> getAssignedStudents() { return assignedStudents; }
    public void setAssignedStudents(List<String> assignedStudents) { this.assignedStudents = assignedStudents; }

    public void addAssignedStudent(String studentId) {
        if (!assignedStudents.contains(studentId)) {
            assignedStudents.add(studentId);
        }
    }

    public void removeAssignedStudent(String studentId) {
        assignedStudents.remove(studentId);
    }

    public List<Schedule> getCareSchedule() { return careSchedule; }
    public void setCareSchedule(List<Schedule> careSchedule) { this.careSchedule = careSchedule; }
    public void addCareSchedule(Schedule schedule) { this.careSchedule.add(schedule); }
    public void removeCareSchedule(Schedule schedule) { this.careSchedule.remove(schedule); }

    @Override
    public String toString() {
        return String.format("Dr. %s %s - Specialty: %s - License: %s",
                getFirstName(), getLastName(), specialty, licenseNumber);
    }
}
