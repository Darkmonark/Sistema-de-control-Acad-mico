package com.hospital.sanrafael.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void ensureTablesExist() {
        if (!DatabaseConnection.testConnection()) {
            System.out.println("PostgreSQL not available — will use local files");
            return;
        }

        try {
            InputStream input = DatabaseInitializer.class.getClassLoader().getResourceAsStream("database/script.sql");
            if (input == null) {
                input = DatabaseInitializer.class.getClassLoader().getResourceAsStream("../database/script.sql");
            }
            if (input == null) {
                System.out.println("SQL script not found in classpath, trying filesystem...");
                executeFromFileSystem();
                return;
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().startsWith("--") && !line.trim().startsWith("SELECT") && !line.trim().startsWith("DROP")) {
                        sb.append(line).append("\n");
                    }
                }
            }

            String[] statements = sb.toString().split(";");
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                int count = 0;
                for (String s : statements) {
                    String sql = s.trim();
                    if (!sql.isEmpty()) {
                        try {
                            stmt.execute(sql);
                            count++;
                        } catch (Exception e) {
                            // Table already exists — ignore
                        }
                    }
                }
                System.out.println("Database tables verified (" + count + " statements)");
            }
        } catch (Exception e) {
            System.out.println("Could not initialize database tables: " + e.getMessage());
        }
    }

    private static void executeFromFileSystem() {
        String[] paths = {"database/script.sql", "src/main/resources/database/script.sql", "../database/script.sql"};
        for (String path : paths) {
            try (BufferedReader reader = new BufferedReader(new java.io.FileReader(path))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().startsWith("--") && !line.trim().startsWith("SELECT") && !line.trim().startsWith("DROP")) {
                        sb.append(line).append("\n");
                    }
                }
                String[] statements = sb.toString().split(";");
                try (Connection conn = DatabaseConnection.getConnection();
                     Statement stmt = conn.createStatement()) {
                    for (String s : statements) {
                        String sql = s.trim();
                        if (!sql.isEmpty()) {
                            try { stmt.execute(sql); } catch (Exception e) { }
                        }
                    }
                }
                System.out.println("Database tables verified from: " + path);
                return;
            } catch (Exception e) { }
        }
    }
}
