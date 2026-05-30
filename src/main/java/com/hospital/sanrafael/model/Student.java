package com.hospital.sanrafael.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Student extends Person implements Serializable {
private static final long serialVersionUID = 2L;

private String career;
private int semester;
private Shift shift;
private List<Subject> subjects;
private List<Schedule> weeklySchedule;
private LocalDate arlExpirationDate;
private String assignedDoctorId;

    public Student() {
        this.subjects = new ArrayList<>();
        this.weeklySchedule = new ArrayList<>();
    }

public Student(String id, String firstName, String lastName, String email, String phone,
String birthDate, String gender, String address,
String career, int semester, Shift shift) {
super(id, firstName, lastName, email, phone, birthDate, gender, address);
this.career = career;
this.semester = semester;
this.shift = shift;
this.subjects = new ArrayList<>();
this.weeklySchedule = new ArrayList<>();
}

public Student(String id, String firstName, String lastName, String email, String phone,
String birthDate, String gender, String address,
String career, int semester, Shift shift, LocalDate arlExpirationDate, String assignedDoctorId) {
super(id, firstName, lastName, email, phone, birthDate, gender, address);
this.career = career;
this.semester = semester;
this.shift = shift;
this.subjects = new ArrayList<>();
this.weeklySchedule = new ArrayList<>();
this.arlExpirationDate = arlExpirationDate;
this.assignedDoctorId = assignedDoctorId;
}

    public String getCareer() { return career; }
    public void setCareer(String career) { this.career = career; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public Shift getShift() { return shift; }
    public void setShift(Shift shift) { this.shift = shift; }

    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { this.subjects = subjects; }
    public void addSubject(Subject subject) { this.subjects.add(subject); }

    public void removeSubject(String subjectCode) {
        subjects.removeIf(s -> s.getCode().equals(subjectCode));
    }

public List<Schedule> getWeeklySchedule() { return weeklySchedule; }
public void setWeeklySchedule(List<Schedule> weeklySchedule) { this.weeklySchedule = weeklySchedule; }
public void addSchedule(Schedule schedule) { this.weeklySchedule.add(schedule); }

public LocalDate getArlExpirationDate() { return arlExpirationDate; }
public void setArlExpirationDate(LocalDate arlExpirationDate) { this.arlExpirationDate = arlExpirationDate; }

public String getAssignedDoctorId() { return assignedDoctorId; }
public void setAssignedDoctorId(String assignedDoctorId) { this.assignedDoctorId = assignedDoctorId; }

public void generateScheduleFromSubjects() {
        this.weeklySchedule.clear();
        for (Subject subject : subjects) {
            for (Schedule schedule : subject.getSchedules()) {
                this.weeklySchedule.add(new Schedule(
                        schedule.getDay(),
                        schedule.getStartTime(),
                        schedule.getEndTime(),
                        subject.getName(),
                        subject.getProfessor(),
                        schedule.getClassroom()
                ));
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Student: %s %s - Career: %s - Semester: %d",
                getFirstName(), getLastName(), career, semester);
    }
}
