package com.hospital.sanrafael.dao;

import com.hospital.sanrafael.database.DatabaseConnection;
import com.hospital.sanrafael.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreUserDAO {

    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM usuarios";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (Exception e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }

    public User getByUsername(String username) {
        String query = "SELECT * FROM usuarios WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(User user) {
        String query = """
            INSERT INTO usuarios (username, email, password_hash, full_name, role)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole());
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error saving to database: " + e.getMessage(), e);
        }
    }

    public void update(User user) {
        String query = """
            UPDATE usuarios
            SET email = ?, password_hash = ?, full_name = ?, role = ?
            WHERE username = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getUsername());
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error updating in database: " + e.getMessage(), e);
        }
    }

    public void delete(String username) {
        String query = "DELETE FROM usuarios WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error deleting from database: " + e.getMessage(), e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getString("full_name"),
            rs.getString("role")
        );
    }
}
