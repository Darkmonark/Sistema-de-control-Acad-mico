package com.hospital.sanrafael.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseCreator {

    private static final String HOST = "localhost";
    private static final String PORT = "5432";
    private static final String DB_NAME = "hospital_san_rafael";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";

    private static final String BASE_URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/postgres";

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  Database Creator");
        System.out.println("  Hospital San Rafael");
        System.out.println("==========================================\n");

        try {
            createDatabase();
            Connection conn = connectToDatabase();

            if (conn != null) {
                System.out.println("\nDatabase ready to use!");
                System.out.println("   Name: " + DB_NAME);
                System.out.println("   User: " + USERNAME);
                System.out.println("\nYou can now run the JavaFX application.");
                conn.close();
            }

        } catch (Exception e) {
            System.err.println("\nError: " + e.getMessage());
            System.out.println("\nSolution:");
            System.out.println("1. Verify PostgreSQL is installed");
            System.out.println("2. Verify PostgreSQL is running");
            System.out.println("3. Check that the password is correct");
            System.out.println("4. Run pgAdmin and create the DB manually");
            e.printStackTrace();
        }
    }

    private static void createDatabase() throws SQLException {
        System.out.println("Step 1: Checking/Creating database...");

        String sql = "SELECT datname FROM pg_database WHERE datname = '" + DB_NAME + "'";

        try (Connection conn = DriverManager.getConnection(BASE_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {

            var rs = stmt.executeQuery(sql);

            if (rs.next()) {
                System.out.println("Database '" + DB_NAME + "' already exists");
            } else {
                System.out.println("Creating database '" + DB_NAME + "'...");

                String createDB = "CREATE DATABASE " + DB_NAME +
                    " WITH OWNER = " + USERNAME +
                    " ENCODING = 'UTF8'" +
                    " LC_COLLATE = 'Spanish_Spain.1252'" +
                    " LC_CTYPE = 'Spanish_Spain.1252'" +
                    " CONNECTION LIMIT = -1";

                stmt.execute(createDB);
                System.out.println("Database created successfully");
            }
        }
    }

    private static Connection connectToDatabase() throws SQLException, IOException {
        System.out.println("\nStep 2: Connecting to the database...");

        String dbUrl = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;

        Connection conn = DriverManager.getConnection(dbUrl, USERNAME, PASSWORD);
        System.out.println("Successful connection to: " + dbUrl);

        System.out.println("\nStep 3: Executing table script...");
        executeScript(conn, "database/script.sql");

        return conn;
    }

    private static void executeScript(Connection conn, String scriptPath) throws SQLException, IOException {
        String[] possiblePaths = {
            scriptPath,
            "src/main/resources/" + scriptPath,
            "database/" + scriptPath,
            "../database/" + scriptPath
        };

        String content = null;
        for (String path : possiblePaths) {
            try {
                content = readFile(path);
                if (content != null) {
                    System.out.println("   Script found at: " + path);
                    break;
                }
            } catch (IOException e) {
            }
        }

        if (content == null) {
            System.out.println("SQL script not found. Execute manually: database/script.sql");
            return;
        }

        String[] statements = content.split(";");

        int successful = 0;
        int errors = 0;

        try (Statement stmt = conn.createStatement()) {
            for (String statement : statements) {
                String sql = statement.trim();
                if (!sql.isEmpty() && !sql.startsWith("--")) {
                    try {
                        stmt.execute(sql);
                        successful++;
                    } catch (SQLException e) {
                        if (!e.getMessage().contains("already exists") &&
                            !e.getMessage().contains("ya existe")) {
                            errors++;
                        }
                    }
                }
            }
        }

        System.out.println("   Statements executed: " + successful);
        if (errors > 0) {
            System.out.println("   Errors ignored: " + errors);
        }
        System.out.println("Script executed successfully");
    }

    private static String readFile(String path) throws IOException {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        return content.toString();
    }

    public static boolean verifyConnection() {
        try {
            String dbUrl = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;
            Connection conn = DriverManager.getConnection(dbUrl, USERNAME, PASSWORD);
            conn.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
