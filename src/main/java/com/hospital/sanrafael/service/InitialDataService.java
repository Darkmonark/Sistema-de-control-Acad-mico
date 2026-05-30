package com.hospital.sanrafael.service;

import com.hospital.sanrafael.model.Student;
import com.hospital.sanrafael.model.Doctor;
import com.hospital.sanrafael.model.Subject;
import com.hospital.sanrafael.model.Schedule;
import com.hospital.sanrafael.model.Shift;

public class InitialDataService {

    private final StudentService studentService;
    private final DoctorService doctorService;
    private final SubjectService subjectService;

    public InitialDataService() {
        studentService = new StudentService();
        doctorService = new DoctorService();
        subjectService = new SubjectService();
    }

    public void loadExampleData() {
        loadExampleDoctors();
        loadExampleSubjects();
        loadExampleStudents();
    }

    private void loadExampleDoctors() {
        if (doctorService.getAllDoctors().isEmpty()) {
            Doctor doctor1 = new Doctor(
                "D001", "Carlos", "Mendoza", "carlos.mendoza@hospital.com", "555-0101",
                "1985-03-15", "M", "Av. Principal 123",
                "Internal Medicine", "COL-12345", "Pabellón A", 10
            );

            Doctor doctor2 = new Doctor(
                "D002", "Ana", "Rodríguez", "ana.rodriguez@hospital.com", "555-0102",
                "1990-07-22", "F", "Calle 45 #678",
                "Pediatrics", "COL-67890", "Pabellón B", 7
            );

            Doctor doctor3 = new Doctor(
                "D003", "Luis", "García", "luis.garcia@hospital.com", "555-0103",
                "1982-11-30", "M", "Carrera 15 #90",
                "General Surgery", "COL-11111", "Quirófano", 12
            );

            Doctor doctor4 = new Doctor(
                "D004", "María", "López", "maria.lopez@hospital.com", "555-0104",
                "1988-05-18", "F", "Transversal 20 #45",
                "Gynecology", "COL-22222", "Pabellón C", 8
            );

            Doctor doctor5 = new Doctor(
                "D005", "Roberto", "Silva", "roberto.silva@hospital.com", "555-0105",
                "1979-09-25", "M", "Calle 100 #15",
                "Cardiology", "COL-33333", "Cardiología", 15
            );

            try {
                doctorService.registerDoctor(doctor1);
                doctorService.registerDoctor(doctor2);
                doctorService.registerDoctor(doctor3);
                doctorService.registerDoctor(doctor4);
                doctorService.registerDoctor(doctor5);

                doctor1.addCareSchedule(new Schedule("Lunes", "08:00", "12:00", "Consulta", "Dr. Carlos Mendoza", "Pabellón A"));
                doctor1.addCareSchedule(new Schedule("Miércoles", "08:00", "12:00", "Consulta", "Dr. Carlos Mendoza", "Pabellón A"));
                doctor1.addCareSchedule(new Schedule("Viernes", "08:00", "12:00", "Consulta", "Dr. Carlos Mendoza", "Pabellón A"));

                doctorService.updateDoctor(doctor1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadExampleSubjects() {
        if (subjectService.getAllSubjects().isEmpty()) {
            Subject subject1 = new Subject("MED101", "Human Anatomy I", "Study of human body anatomy", 4, 1, "Dr. Juan Pérez", "Aula 101");
            Subject subject2 = new Subject("MED102", "Physiology", "Principles of human physiology", 4, 1, "Dra. Laura Gómez", "Aula 102");
            Subject subject3 = new Subject("MED201", "General Pathology", "Introduction to pathology", 5, 2, "Dr. Pedro Sánchez", "Aula 201");
            Subject subject4 = new Subject("MED202", "Pharmacology I", "Fundamentals of pharmacology", 4, 2, "Dra. Carmen Díaz", "Aula 202");
            Subject subject5 = new Subject("MED301", "Internal Medicine I", "Principles of internal medicine", 6, 3, "Dr. Carlos Mendoza", "Aula 301");
            Subject subject6 = new Subject("MED302", "Pediatrics I", "Fundamentals of pediatrics", 5, 3, "Dra. Ana Rodríguez", "Aula 302");
            Subject subject7 = new Subject("MED401", "General Surgery I", "Principles of surgery", 6, 4, "Dr. Luis García", "Quirófano 1");
            Subject subject8 = new Subject("MED402", "Gynecology I", "Fundamentals of gynecology", 5, 4, "Dra. María López", "Aula 402");
            Subject subject9 = new Subject("MED501", "Advanced Cardiology", "Clinical cardiology", 6, 5, "Dr. Roberto Silva", "Cardiología");
            Subject subject10 = new Subject("MED502", "Hospital Practices", "Practices at San Rafael Hospital", 8, 5, "Varios", "Hospital San Rafael");

            try {
                subjectService.registerSubject(subject1);
                subjectService.registerSubject(subject2);
                subjectService.registerSubject(subject3);
                subjectService.registerSubject(subject4);
                subjectService.registerSubject(subject5);
                subjectService.registerSubject(subject6);
                subjectService.registerSubject(subject7);
                subjectService.registerSubject(subject8);
                subjectService.registerSubject(subject9);
                subjectService.registerSubject(subject10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadExampleStudents() {
        if (studentService.getAllStudents().isEmpty()) {
            Student student1 = new Student(
                "E001", "Jos\u00E9", "Mart\u00EDnez", "jose.martinez@student.com", "555-1001",
                "2000-01-15", "M", "Calle 10 #20-30",
                "General Medicine", 1, Shift.MANANA
            );

            Student student2 = new Student(
                "E002", "Sof\u00EDa", "Hern\u00E1ndez", "sofia.hernandez@student.com", "555-1002",
                "2001-03-22", "F", "Carrera 5 #15-40",
                "General Medicine", 2, Shift.MANANA
            );

            Student student3 = new Student(
                "E003", "Diego", "Torres", "diego.torres@student.com", "555-1003",
                "1999-07-10", "M", "Av. 68 #90-10",
                "Nursing", 3, Shift.TARDE
            );

            Student student4 = new Student(
                "E004", "Valentina", "Ram\u00EDrez", "valentina.ramirez@student.com", "555-1004",
                "2000-11-05", "F", "Calle 72 #15-30",
                "General Medicine", 4, Shift.MANANA
            );

            Student student5 = new Student(
                "E005", "Andr\u00E9s", "Vargas", "andres.vargas@student.com", "555-1005",
                "1998-09-18", "M", "Transversal 15 #80-20",
                "Nursing", 5, Shift.NOCHE
            );

            try {
                studentService.registerStudent(student1);
                studentService.registerStudent(student2);
                studentService.registerStudent(student3);
                studentService.registerStudent(student4);
                studentService.registerStudent(student5);

                Subject subject1 = subjectService.getSubjectByCode("MED101");
                Subject subject2 = subjectService.getSubjectByCode("MED102");

                if (subject1 != null && subject2 != null) {
                    student1.addSubject(subject1);
                    student1.addSubject(subject2);
                    student1.generateScheduleFromSubjects();
                    studentService.updateStudent(student1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
