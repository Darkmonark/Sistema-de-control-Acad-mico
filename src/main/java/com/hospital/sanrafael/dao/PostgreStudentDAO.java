package com.hospital.sanrafael.dao;

import com.hospital.sanrafael.database.DatabaseConnection;
import com.hospital.sanrafael.model.Shift;
import com.hospital.sanrafael.model.Student;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class PostgreStudentDAO {

    public List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        String query = """
            SELECT p.*, e.carrera, e.semestre, e.turno, e.estado
            FROM persona p
            JOIN estudiante e ON p.id = e.id_persona
            WHERE p.tipo_persona = 'ESTUDIANTE'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (Exception e) {
            System.err.println("Error fetching students: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    public Student getById(String id) {
        String query = """
            SELECT p.*, e.carrera, e.semestre, e.turno, e.estado
            FROM persona p
            JOIN estudiante e ON p.id = e.id_persona
            WHERE p.id = ? AND p.tipo_persona = 'ESTUDIANTE'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Student student) {
        String insertPersona = """
            INSERT INTO persona (id, nombre, apellido, email, telefono, fecha_nacimiento, genero, direccion, tipo_persona)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ESTUDIANTE')
            """;

        String insertStudent = """
            INSERT INTO estudiante (id_persona, carrera, semestre, turno)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            System.out.println("Saving student: " + student.getId() + " - " + student.getFirstName());

            try (PreparedStatement stmt1 = conn.prepareStatement(insertPersona)) {
                stmt1.setString(1, student.getId());
                stmt1.setString(2, student.getFirstName());
                stmt1.setString(3, student.getLastName());
                stmt1.setString(4, student.getEmail());
                stmt1.setString(5, student.getPhone());

                stmt1.setDate(6, parseDateSafe(student.getBirthDate()));

                stmt1.setString(7, student.getGender());
                stmt1.setString(8, student.getAddress());
                int rows1 = stmt1.executeUpdate();
                System.out.println("   -> Rows inserted in persona: " + rows1);
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(insertStudent)) {
                stmt2.setString(1, student.getId());
                stmt2.setString(2, student.getCareer());
                stmt2.setInt(3, student.getSemester());
                stmt2.setString(4, student.getShift() != null ? student.getShift().getDisplayName() : null);
                int rows2 = stmt2.executeUpdate();
                System.out.println("   -> Rows inserted in estudiante: " + rows2);
            }

            conn.commit();
            System.out.println("Student saved successfully in PostgreSQL");

        } catch (Exception e) {
            System.err.println("Error saving student: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error saving to database: " + e.getMessage(), e);
        }
    }

    public void update(Student student) {
        String updatePersona = """
            UPDATE persona
            SET nombre = ?, apellido = ?, email = ?, telefono = ?,
                fecha_nacimiento = ?, genero = ?, direccion = ?
            WHERE id = ?
            """;

        String updateStudent = """
            UPDATE estudiante
            SET carrera = ?, semestre = ?, turno = ?
            WHERE id_persona = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(updatePersona)) {
                stmt1.setString(1, student.getFirstName());
                stmt1.setString(2, student.getLastName());
                stmt1.setString(3, student.getEmail());
                stmt1.setString(4, student.getPhone());
                stmt1.setDate(5, parseDateSafe(student.getBirthDate()));
                stmt1.setString(6, student.getGender());
                stmt1.setString(7, student.getAddress());
                stmt1.setString(8, student.getId());
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(updateStudent)) {
                stmt2.setString(1, student.getCareer());
                stmt2.setInt(2, student.getSemester());
                stmt2.setString(3, student.getShift() != null ? student.getShift().getDisplayName() : null);
                stmt2.setString(4, student.getId());
                stmt2.executeUpdate();
            }

            conn.commit();
            System.out.println("Student updated successfully");

        } catch (Exception e) {
            System.err.println("Error updating student: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error updating in database: " + e.getMessage(), e);
        }
    }

    public void delete(String id) {
        String query = "DELETE FROM persona WHERE id = ? AND tipo_persona = 'ESTUDIANTE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);
            int rows = stmt.executeUpdate();
            System.out.println("   -> Rows deleted: " + rows);
            System.out.println("Student deleted successfully");
        } catch (Exception e) {
            System.err.println("Error deleting student: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error deleting from database: " + e.getMessage(), e);
        }
    }

    private java.sql.Date parseDateSafe(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        dateStr = dateStr.trim();
        String[] formats = {"yyyy-MM-dd", "dd/MM/yyyy", "yyyy/MM/dd", "dd-MM-yyyy"};
        for (String fmt : formats) {
            try {
                return java.sql.Date.valueOf(LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(fmt)));
            } catch (DateTimeParseException e) {}
        }
        try {
            return java.sql.Date.valueOf(dateStr);
        } catch (IllegalArgumentException e) {
            System.err.println("Cannot parse date: " + dateStr);
            return null;
        }
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
            rs.getString("id"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("email"),
            rs.getString("telefono"),
            rs.getString("fecha_nacimiento") != null ? rs.getString("fecha_nacimiento") : "",
            rs.getString("genero"),
            rs.getString("direccion"),
            rs.getString("carrera"),
            rs.getInt("semestre"),
            Shift.fromDisplayName(rs.getString("turno"))
        );
    }
}
