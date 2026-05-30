package com.hospital.sanrafael.service;

import com.hospital.sanrafael.dao.PostgreStudentDAO;
import com.hospital.sanrafael.dao.StudentDAO;
import com.hospital.sanrafael.database.DatabaseConnection;
import com.hospital.sanrafael.model.Student;
import com.hospital.sanrafael.model.Schedule;
import com.hospital.sanrafael.model.Subject;

import java.time.LocalDate;
import java.util.List;

public class StudentService {
    private final StudentDAO fileDAO;
    private final PostgreStudentDAO dbDAO;
    private final boolean useDatabase;

    public StudentService() {
        this.fileDAO = new StudentDAO();
        this.dbDAO = new PostgreStudentDAO();
        this.useDatabase = DatabaseConnection.testConnection();
    }

    public List<Student> getAllStudents() {
        return useDatabase ? dbDAO.getAll() : fileDAO.getAll();
    }

    public Student getStudentById(String id) {
        return useDatabase ? dbDAO.getById(id) : fileDAO.getById(id);
    }

    public Student registerStudent(Student student) {
        student.setId(generateNextStudentId());
        if (useDatabase) {
            dbDAO.save(student);
        } else {
            fileDAO.save(student);
        }
        return student;
    }

    private String generateNextStudentId() {
        List<Student> all = getAllStudents();
        int max = 0;
        for (Student s : all) {
            String id = s.getId();
            if (id != null && id.startsWith("E")) {
                try {
                    int n = Integer.parseInt(id.substring(1));
                    if (n > max) max = n;
                } catch (NumberFormatException e) {}
            }
        }
        return "E" + String.format("%03d", max + 1);
    }

    public Student updateStudent(Student student) {
        if (useDatabase) {
            dbDAO.update(student);
        } else {
            fileDAO.update(student);
        }
        return student;
    }

    public void deleteStudent(String id) {
        if (useDatabase) {
            dbDAO.delete(id);
        } else {
            fileDAO.delete(id);
        }
    }

    public List<Student> searchByCareer(String career) {
        return getAllStudents();
    }

    public List<Student> searchBySemester(int semester) {
        return getAllStudents();
    }

    public void addSubjectToStudent(String studentId, Subject subject) {
        Student student = getStudentById(studentId);
        if (student != null) {
            student.addSubject(subject);
            student.generateScheduleFromSubjects();
            updateStudent(student);
        }
    }

    public void removeSubjectFromStudent(String studentId, String subjectCode) {
        Student student = getStudentById(studentId);
        if (student != null) {
            student.removeSubject(subjectCode);
            student.generateScheduleFromSubjects();
            updateStudent(student);
        }
    }

public List<Schedule> getStudentSchedule(String studentId) {
Student student = getStudentById(studentId);
if (student != null) {
return student.getWeeklySchedule();
}
return List.of();
}

public void checkAndUpdateArlStatus(String studentId, LocalDate arlExpirationDate) {
Student student = getStudentById(studentId);
if (student != null) {
student.setArlExpirationDate(arlExpirationDate);
updateStudent(student);
}
}

public void checkAllArlExpirations() {
List<Student> students = getAllStudents();
AlertService alertService = AlertService.getInstance();
alertService.checkArlExpirations(students);
}
}
