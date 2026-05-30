package com.hospital.sanrafael.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnection {
    private static Connection connection = null;
    private static Properties properties = new Properties();
    private static boolean initialized = false;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        if (initialized) return;

        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.out.println("Using default configuration");
        }
        initialized = true;
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            String url = properties.getProperty("db.url", "jdbc:postgresql://localhost:5432/hospital_san_rafael");
            String username = properties.getProperty("db.username", "postgres");

            String[] passwordsToTry = {"admin", "8253", "postgres", "password"};
            String passwordFromConfig = properties.getProperty("db.password", "admin");

            passwordsToTry = new String[] {passwordFromConfig, "admin", "8253", "postgres", "password"};

            Exception lastException = null;

            for (String password : passwordsToTry) {
                try {
                    String driver = properties.getProperty("db.driver", "org.postgresql.Driver");
                    Class.forName(driver);
                    connection = DriverManager.getConnection(url, username, password);

                    System.out.println("Successful connection to PostgreSQL!");
                    System.out.println("   URL: " + url);
                    System.out.println("   User: " + username);
                    System.out.println("   Password: " + password);
                    System.out.println("   Database: hospital_san_rafael");

                    return connection;
                } catch (SQLException e) {
                    lastException = e;
                }
            }

            throw new SQLException("No password worked. Last error: " +
                (lastException != null ? lastException.getMessage() : "Unknown"));
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean isConnected = conn != null && !conn.isClosed();
            if (isConnected) {
                System.out.println("PostgreSQL connected successfully");
            }
            return isConnected;
        } catch (Exception e) {
            System.err.println("Connection error: " + e.getMessage());
            System.err.println("   The application will use local storage (.dat files)");
            return false;
        }
    }
}
