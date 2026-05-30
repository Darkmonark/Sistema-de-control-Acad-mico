package com.hospital.sanrafael.dao;

import com.hospital.sanrafael.database.DatabaseConnection;
import com.hospital.sanrafael.model.Doctor;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class PostgreDoctorDAO {

    public List<Doctor> getAll() {
        List<Doctor> doctors = new ArrayList<>();
        String query = """
            SELECT p.*, d.especialidad, d.numero_colegiado, d.area_asignada, d.anios_experiencia
            FROM persona p
            JOIN doctor d ON p.id = d.id_persona
            WHERE p.tipo_persona = 'DOCTOR'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        } catch (Exception e) {
            System.err.println("Error fetching doctors: " + e.getMessage());
        }
        return doctors;
    }

    public Doctor getById(String id) {
        String query = """
            SELECT p.*, d.especialidad, d.numero_colegiado, d.area_asignada, d.anios_experiencia
            FROM persona p
            JOIN doctor d ON p.id = d.id_persona
            WHERE p.id = ? AND p.tipo_persona = 'DOCTOR'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDoctor(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Doctor doctor) {
        String insertPersona = """
            INSERT INTO persona (id, nombre, apellido, email, telefono, fecha_nacimiento, genero, direccion, tipo_persona)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'DOCTOR')
            """;

        String insertDoctor = """
            INSERT INTO doctor (id_persona, especialidad, numero_colegiado, area_asignada, anios_experiencia)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(insertPersona)) {
                stmt1.setString(1, doctor.getId());
                stmt1.setString(2, doctor.getFirstName());
                stmt1.setString(3, doctor.getLastName());
                stmt1.setString(4, doctor.getEmail());
                stmt1.setString(5, doctor.getPhone());
                stmt1.setDate(6, parseDateSafe(doctor.getBirthDate()));
                stmt1.setString(7, doctor.getGender());
                stmt1.setString(8, doctor.getAddress());
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(insertDoctor)) {
                stmt2.setString(1, doctor.getId());
                stmt2.setString(2, doctor.getSpecialty());
                stmt2.setString(3, doctor.getLicenseNumber());
                stmt2.setString(4, doctor.getAssignedArea());
                stmt2.setInt(5, doctor.getYearsExperience());
                stmt2.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            System.err.println("Error saving doctor: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error saving to database: " + e.getMessage(), e);
        }
    }

    public void update(Doctor doctor) {
        String updatePersona = """
            UPDATE persona
            SET nombre = ?, apellido = ?, email = ?, telefono = ?,
                fecha_nacimiento = ?, genero = ?, direccion = ?
            WHERE id = ?
            """;

        String updateDoctor = """
            UPDATE doctor
            SET especialidad = ?, numero_colegiado = ?, area_asignada = ?, anios_experiencia = ?
            WHERE id_persona = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(updatePersona)) {
                stmt1.setString(1, doctor.getFirstName());
                stmt1.setString(2, doctor.getLastName());
                stmt1.setString(3, doctor.getEmail());
                stmt1.setString(4, doctor.getPhone());
                stmt1.setString(5, doctor.getBirthDate());
                stmt1.setString(6, doctor.getGender());
                stmt1.setString(7, doctor.getAddress());
                stmt1.setString(8, doctor.getId());
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(updateDoctor)) {
                stmt2.setString(1, doctor.getSpecialty());
                stmt2.setString(2, doctor.getLicenseNumber());
                stmt2.setString(3, doctor.getAssignedArea());
                stmt2.setInt(4, doctor.getYearsExperience());
                stmt2.setString(5, doctor.getId());
                stmt2.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            System.err.println("Error updating doctor: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error updating in database: " + e.getMessage(), e);
        }
    }

    public void delete(String id) {
        String query = "DELETE FROM persona WHERE id = ? AND tipo_persona = 'DOCTOR'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error deleting doctor: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error deleting from database: " + e.getMessage(), e);
        }
    }

    public List<Doctor> getBySpecialty(String specialty) {
        List<Doctor> doctors = new ArrayList<>();
        String query = """
            SELECT p.*, d.especialidad, d.numero_colegiado, d.area_asignada, d.anios_experiencia
            FROM persona p
            JOIN doctor d ON p.id = d.id_persona
            WHERE p.tipo_persona = 'DOCTOR' AND d.especialidad = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, specialty);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doctors;
    }

    public Doctor getByLicenseNumber(String licenseNumber) {
        String query = """
            SELECT p.*, d.especialidad, d.numero_colegiado, d.area_asignada, d.anios_experiencia
            FROM persona p
            JOIN doctor d ON p.id = d.id_persona
            WHERE p.tipo_persona = 'DOCTOR' AND d.numero_colegiado = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, licenseNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDoctor(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        return new Doctor(
            rs.getString("id"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("email"),
            rs.getString("telefono"),
            rs.getString("fecha_nacimiento") != null ? rs.getString("fecha_nacimiento") : "",
            rs.getString("genero"),
            rs.getString("direccion"),
            rs.getString("especialidad"),
            rs.getString("numero_colegiado"),
            rs.getString("area_asignada"),
            rs.getInt("anios_experiencia")
        );
    }
}
