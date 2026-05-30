package com.hospital.sanrafael.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseTester {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  Testing PostgreSQL connection");
        System.out.println("  Hospital San Rafael");
        System.out.println("==========================================\n");

        int[] ports = {5432, 5433};
        String[] passwords = {"admin", "postgres", "password"};

        for (int port : ports) {
            for (String password : passwords) {
                testConnection(port, password);
            }
        }
    }

    private static void testConnection(int port, String password) {
        String url = "jdbc:postgresql://localhost:" + port + "/hospital_san_rafael";
        String username = "postgres";
        String driver = "org.postgresql.Driver";

        System.out.println("Testing: " + url + " (pass: " + password + ")");

        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, username, password);

            System.out.println("  CONNECTION SUCCESSFUL!");
            System.out.println("  Port: " + port);
            System.out.println("  Password: " + password);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM persona");
            rs.next();
            int count = rs.getInt(1);
            System.out.println("  Tables found: " + count + " records in persona");

            rs.close();
            stmt.close();
            conn.close();

            System.out.println("\n  ==> UPDATE database.properties WITH:");
            System.out.println("      db.url=jdbc:postgresql://localhost:" + port + "/hospital_san_rafael");
            System.out.println("      db.password=" + password);
            System.out.println("\n==========================================\n");

        } catch (Exception e) {
            System.out.println("  Failed: " + e.getMessage());
        }
    }
}
