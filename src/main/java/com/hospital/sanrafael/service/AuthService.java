package com.hospital.sanrafael.service;

import com.hospital.sanrafael.dao.GenericDAO;
import com.hospital.sanrafael.dao.PostgreUserDAO;
import com.hospital.sanrafael.database.DatabaseConnection;
import com.hospital.sanrafael.model.User;
import java.util.List;

public class AuthService {
private static final String USERS_FILE = "usuarios.dat";
private final GenericDAO<User> fileDAO;
private final PostgreUserDAO dbDAO;
private final boolean useDatabase;

public AuthService() {
this.fileDAO = new GenericDAO<>(USERS_FILE, User.class);
this.dbDAO = new PostgreUserDAO();
this.useDatabase = DatabaseConnection.testConnection();
createDefaultUsers();
}

private void createDefaultUsers() {
List<User> users = getAllUsers();
boolean isEmpty = users.isEmpty();

if (isEmpty) {
// Administrador
User admin = new User("admin", "admin@hospital.com", PasswordUtils.hashPassword("admin123"), "Administrador del Sistema", "Administrador");
saveUser(admin);

// Doctor
User doctor = new User("doctor1", "carlos.mendoza@hospital.com", PasswordUtils.hashPassword("doctor123"), "Carlos Mendoza", "Doctor");
saveUser(doctor);

// Estudiante
User student = new User("estudiante1", "juan.perez@estudiante.com", PasswordUtils.hashPassword("estudiante123"), "Juan Pérez", "Estudiante");
saveUser(student);

System.out.println("Usuarios por defecto creados exitosamente");
}
}

private void saveUser(User user) {
if (useDatabase) {
dbDAO.save(user);
} else {
fileDAO.save(user);
}
}

    public User login(String username, String password) {
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getUsername().equals(username) && PasswordUtils.verifyPassword(password, u.getPassword())) {
                return u;
            }
        }
        return null;
    }

    public boolean register(String username, String email, String password, String fullName, String role) {
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return false;
            }
        }
        User newUser = new User(username, email, PasswordUtils.hashPassword(password), fullName, role);
        if (useDatabase) {
            dbDAO.save(newUser);
        } else {
            fileDAO.save(newUser);
        }
        return true;
    }

    public boolean usernameExists(String username) {
        List<User> users = getAllUsers();
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    public User getUser(String username) {
        if (useDatabase) {
            return dbDAO.getByUsername(username);
        }
        List<User> users = fileDAO.getAll();
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

private List<User> getAllUsers() {
return useDatabase ? dbDAO.getAll() : fileDAO.getAll();
}

public boolean isAuthenticated() {
return useDatabase || fileDAO.getAll().size() > 0;
}
}
