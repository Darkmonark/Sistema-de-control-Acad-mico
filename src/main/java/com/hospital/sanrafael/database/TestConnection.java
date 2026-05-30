package com.hospital.sanrafael.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("================================");
        System.out.println("Testing PostgreSQL Connection");
        System.out.println("================================\n");

        try {
            Connection conn = DatabaseConnection.getConnection();

            if (conn != null && !conn.isClosed()) {
                System.out.println("Connection Successful!");

                System.out.println("\nConnection Info:");
                System.out.println("  URL: " + conn.getMetaData().getURL());
                System.out.println("  User: " + conn.getMetaData().getUserName());
                System.out.println("  Product: " + conn.getMetaData().getDatabaseProductName() +
                                   " " + conn.getMetaData().getDatabaseProductVersion());

                System.out.println("\nRecords in database:");
                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("""
                    SELECT
                        (SELECT COUNT(*) FROM persona WHERE tipo_persona = 'ESTUDIANTE') AS estudiantes,
                        (SELECT COUNT(*) FROM persona WHERE tipo_persona = 'DOCTOR') AS doctores,
                        (SELECT COUNT(*) FROM materia) AS materias,
                        (SELECT COUNT(*) FROM horario) AS horarios
                    """);

                if (rs.next()) {
                    System.out.println("  Students: " + rs.getInt("estudiantes"));
                    System.out.println("  Doctors: " + rs.getInt("doctores"));
                    System.out.println("  Subjects: " + rs.getInt("materias"));
                    System.out.println("  Schedules: " + rs.getInt("horarios"));
                }

                System.out.println("\nFirst 5 students:");
                ResultSet rs2 = stmt.executeQuery("""
                    SELECT p.nombre, p.apellido, e.carrera, e.semestre
                    FROM persona p
                    JOIN estudiante e ON p.id = e.id_persona
                    WHERE p.tipo_persona = 'ESTUDIANTE'
                    LIMIT 5
                    """);

                while (rs2.next()) {
                    System.out.println("  - " + rs2.getString("nombre") + " " +
                                     rs2.getString("apellido") +
                                     " (" + rs2.getString("carrera") +
                                     " - " + rs2.getInt("semestre") + " semester)");
                }

                rs2.close();
                rs.close();
                stmt.close();

                System.out.println("\n================================");
                System.out.println("Everything works correctly!");
                System.out.println("================================");
            } else {
                System.out.println("Could not establish connection");
            }

            DatabaseConnection.closeConnection();

        } catch (Exception e) {
            System.out.println("\nConnection error:");
            System.out.println("  " + e.getClass().getSimpleName() + ": " + e.getMessage());
            System.out.println("\nPossible solutions:");
            System.out.println("  1. Verify PostgreSQL is installed");
            System.out.println("  2. Verify database 'hospital_san_rafael' exists");
            System.out.println("  3. Check database.properties file");
            System.out.println("  4. Run the SQL script in database/script.sql");
            e.printStackTrace();
        }
    }
}
