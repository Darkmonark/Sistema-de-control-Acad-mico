package com.hospital.sanrafael.dao;

import com.hospital.sanrafael.database.DatabaseConnection;
import com.hospital.sanrafael.model.Subject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreSubjectDAO {

    public List<Subject> getAll() {
        List<Subject> subjects = new ArrayList<>();
        String query = "SELECT * FROM materia";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                subjects.add(mapResultSetToSubject(rs));
            }
        } catch (Exception e) {
            System.err.println("Error fetching subjects: " + e.getMessage());
        }
        return subjects;
    }

    public Subject getByCode(String code) {
        String query = "SELECT * FROM materia WHERE codigo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSubject(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Subject subject) {
        String query = """
            INSERT INTO materia (codigo, nombre, descripcion, creditos, semestre_recomendado, profesor_responsable, aula)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, subject.getCode());
            stmt.setString(2, subject.getName());
            stmt.setString(3, subject.getDescription());
            stmt.setInt(4, subject.getCredits());
            stmt.setInt(5, subject.getRecommendedSemester());
            stmt.setString(6, subject.getProfessor());
            stmt.setString(7, subject.getClassroom());
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error saving subject: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error saving to database: " + e.getMessage(), e);
        }
    }

    public void update(Subject subject) {
        String query = """
            UPDATE materia
            SET nombre = ?, descripcion = ?, creditos = ?, semestre_recomendado = ?,
                profesor_responsable = ?, aula = ?
            WHERE codigo = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, subject.getName());
            stmt.setString(2, subject.getDescription());
            stmt.setInt(3, subject.getCredits());
            stmt.setInt(4, subject.getRecommendedSemester());
            stmt.setString(5, subject.getProfessor());
            stmt.setString(6, subject.getClassroom());
            stmt.setString(7, subject.getCode());
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error updating subject: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error updating in database: " + e.getMessage(), e);
        }
    }

    public void delete(String code) {
        String query = "DELETE FROM materia WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, code);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error deleting subject: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error deleting from database: " + e.getMessage(), e);
        }
    }

    public List<Subject> getBySemester(int semester) {
        List<Subject> subjects = new ArrayList<>();
        String query = "SELECT * FROM materia WHERE semestre_recomendado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, semester);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                subjects.add(mapResultSetToSubject(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subjects;
    }

    public List<Subject> getByProfessor(String professor) {
        List<Subject> subjects = new ArrayList<>();
        String query = "SELECT * FROM materia WHERE profesor_responsable = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, professor);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                subjects.add(mapResultSetToSubject(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subjects;
    }

    private Subject mapResultSetToSubject(ResultSet rs) throws SQLException {
        return new Subject(
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getString("descripcion") != null ? rs.getString("descripcion") : "",
            rs.getInt("creditos"),
            rs.getInt("semestre_recomendado"),
            rs.getString("profesor_responsable") != null ? rs.getString("profesor_responsable") : "",
            rs.getString("aula") != null ? rs.getString("aula") : ""
        );
    }
}
