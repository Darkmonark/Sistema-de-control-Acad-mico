package com.hospital.sanrafael.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DebugConnection {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  DEBUG: Testing PostgreSQL Connection");
        System.out.println("  Hospital San Rafael");
        System.out.println("==========================================\n");

        String url = "jdbc:postgresql://localhost:5432/hospital_san_rafael";
        String username = "postgres";
        String password = "admin";
        String driver = "org.postgresql.Driver";

        System.out.println("Attempting connection with:");
        System.out.println("  URL: " + url);
        System.out.println("  User: " + username);
        System.out.println("  Password: " + password);
        System.out.println();

        try {
            System.out.println("1. Loading driver...");
            Class.forName(driver);
            System.out.println("   Driver loaded: " + driver);

            System.out.println("\n2. Connecting to database...");
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("   Connection SUCCESSFUL!");

            System.out.println("\n3. Checking status...");
            if (conn != null && !conn.isClosed()) {
                System.out.println("   Active and valid connection");
            }

            System.out.println("\n4. Server information:");
            System.out.println("   Product Name: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("   Product Version: " + conn.getMetaData().getDatabaseProductVersion());
            System.out.println("   URL: " + conn.getMetaData().getURL());
            System.out.println("   User: " + conn.getMetaData().getUserName());

            System.out.println("\n5. Testing SQL query...");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM persona");
            if (rs.next()) {
                int count = rs.getInt("total");
                System.out.println("   Table 'persona' exists with " + count + " records");
            }

            rs.close();
            stmt.close();
            conn.close();

            System.out.println("\n==========================================");
            System.out.println("  EVERYTHING WORKS CORRECTLY!");
            System.out.println("  The JavaFX application should work");
            System.out.println("==========================================\n");

        } catch (Exception e) {
            System.out.println("\nERROR: " + e.getClass().getSimpleName());
            System.out.println("   Message: " + e.getMessage());
            System.out.println("\nPossible causes:");
            System.out.println("   1. Database does not exist on port 5432");
            System.out.println("   2. Password 'admin' is incorrect");
            System.out.println("   3. PostgreSQL is not running");
            System.out.println("\nError details:");
            e.printStackTrace();
        }
    }
}
