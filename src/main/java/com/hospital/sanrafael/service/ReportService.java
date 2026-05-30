package com.hospital.sanrafael.service;

import com.hospital.sanrafael.model.Notification;
import com.hospital.sanrafael.model.Shift;
import com.hospital.sanrafael.model.Student;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    private static ReportService instance;
    private final StudentService studentService;
    private final AlertService alertService;
    private final NotificationService notificationService;

    private ReportService() {
        studentService = new StudentService();
        alertService = AlertService.getInstance();
        notificationService = NotificationService.getInstance();
    }

    public static synchronized ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    public String generateEntryExitHistory(LocalDate startDate, LocalDate endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== HISTORIAL DE INGRESOS Y SALIDAS ===\n");
        sb.append("Período: ").append(startDate).append(" a ").append(endDate).append("\n\n");

        List<Student> students = studentService.getAllStudents();

        if (students.isEmpty()) {
            sb.append("No hay estudiantes registrados en el sistema.\n");
            return sb.toString();
        }

        int totalEntries = 0;
        for (Student student : students) {
            List<LocalDate> entryDates = getEntryDates(student, startDate, endDate);
            if (!entryDates.isEmpty()) {
                sb.append("Estudiante: ").append(student.getFullName()).append(" (").append(student.getId()).append(")\n");
                sb.append("  Carrera: ").append(student.getCareer()).append(" | Turno: ").append(student.getShift()).append("\n");
                for (LocalDate date : entryDates) {
                    sb.append("  - ").append(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .append(" | Ingreso: 08:00 | Salida: ").append(getExitTimeForDate(student, date)).append("\n");
                    totalEntries++;
                }
                sb.append("\n");
            }
        }
        sb.append("Total de registros: ").append(totalEntries).append("\n");
        return sb.toString();
    }

    public String generateHoursComplianceReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== HORAS CUMPLIDAS VS HORAS REQUERIDAS ===\n");
        sb.append("Semestre actual - Fecha: ").append(LocalDate.now()).append("\n\n");

        List<Student> students = studentService.getAllStudents();
        int requiredHours = 200;

        sb.append(String.format("%-10s %-25s %-15s %-15s %-15s %-10s\n",
                "ID", "Nombre", "Carrera", "Req.", "Cumplidas", "%"));
        sb.append("=".repeat(90)).append("\n");

        for (Student student : students) {
            int hoursCompleted = calculateHoursCompleted(student.getShift());
            double percentage = (hoursCompleted / (double) requiredHours) * 100;
            sb.append(String.format("%-10s %-25s %-15s %-15d %-15d %-10.1f\n",
                    student.getId(),
                    truncate(student.getFullName(), 24),
                    truncate(student.getCareer(), 14),
                    requiredHours,
                    Math.min(hoursCompleted, requiredHours),
                    Math.min(percentage, 100.0)));
        }

        sb.append("\n").append("Total estudiantes: ").append(students.size()).append("\n");
        return sb.toString();
    }

    public String generateCompliancePercentageByGroup(String groupType) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PORCENTAJE DE CUMPLIMIENTO POR ").append(groupType.toUpperCase()).append(" ===\n\n");

        List<Student> students = studentService.getAllStudents();
        Map<String, List<Student>> groups;

        switch (groupType.toLowerCase()) {
            case "carrera":
                groups = students.stream().collect(Collectors.groupingBy(Student::getCareer));
                break;
            case "semestre":
                groups = students.stream().collect(Collectors.groupingBy(s -> "Semestre " + s.getSemester()));
                break;
            case "turno":
                groups = students.stream().collect(Collectors.groupingBy(s -> s.getShift().getDisplayName()));
                break;
            default:
                groups = Map.of("Todos", students);
        }

        int requiredHours = 200;
        sb.append(String.format("%-30s %-15s %-15s %-15s\n", "Grupo", "Estudiantes", "Prom. Horas", "% Cumplimiento"));
        sb.append("=".repeat(75)).append("\n");

        for (Map.Entry<String, List<Student>> entry : groups.entrySet()) {
            List<Student> group = entry.getValue();
            int totalHours = group.stream()
                    .mapToInt(s -> calculateHoursCompleted(s.getShift()))
                    .sum();
            double avgHours = group.isEmpty() ? 0 : (double) totalHours / group.size();
            double compliance = (avgHours / requiredHours) * 100;
            sb.append(String.format("%-30s %-15d %-15.1f %-15.1f\n",
                    truncate(entry.getKey(), 29),
                    group.size(),
                    Math.min(avgHours, requiredHours),
                    Math.min(compliance, 100.0)));
        }

        return sb.toString();
    }

    public String generateShiftViolationsReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTUDIANTES FUERA DE FRANJA HORARIA ===\n");
        sb.append("Fecha: ").append(LocalDate.now()).append("\n\n");

        List<Notification> violations = notificationService.getNotificationsByCategory(
                Notification.Category.SCHEDULE_VIOLATION);

        if (violations.isEmpty()) {
            sb.append("No se han registrado intentos de acceso fuera de horario.\n");
        } else {
            sb.append(String.format("%-10s %-25s %-20s %-15s %s\n",
                    "ID", "Estudiante", "Turno", "Fecha", "Hora Intento"));
            sb.append("=".repeat(85)).append("\n");

            for (Notification n : violations) {
                sb.append(String.format("%-10s %-25s %-20s %-15s %s\n",
                        n.getPersonId() != null ? n.getPersonId() : "N/A",
                        truncate(n.getPersonName() != null ? n.getPersonName() : "N/A", 24),
                        "-",
                        n.getDate() != null ? n.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                        "-"));
            }
        }

        sb.append("\nTotal de violaciones: ").append(violations.size()).append("\n");
        return sb.toString();
    }

    public String generateHistoricalOccupancy() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== OCUPACIÓN HISTÓRICA POR SERVICIO ===\n");
        sb.append("Fecha: ").append(LocalDate.now()).append("\n\n");

        Map<String, Integer> occupancy = getOccupancyByService();
        int total = 0;

        sb.append(String.format("%-30s %-15s %-15s\n", "Servicio", "Estudiantes", "Porcentaje"));
        sb.append("=".repeat(60)).append("\n");

        for (Map.Entry<String, Integer> entry : occupancy.entrySet()) {
            total += entry.getValue();
        }

        for (Map.Entry<String, Integer> entry : occupancy.entrySet()) {
            double pct = total > 0 ? (entry.getValue() * 100.0 / total) : 0;
            sb.append(String.format("%-30s %-15d %-15.1f\n",
                    entry.getKey(), entry.getValue(), pct));
        }

        sb.append("\n").append(String.format("%-30s %-15d\n", "TOTAL", total));
        return sb.toString();
    }

    public String generateArlExpiringReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTUDIANTES CON ARL PRÓXIMA A VENCER ===\n");
        sb.append("Fecha de alerta: ").append(LocalDate.now()).append(" | Ventana: 15 días\n\n");

        List<Student> expiring = getStudentsWithExpiringARL(15);

        if (expiring.isEmpty()) {
            sb.append("No hay estudiantes con ARL próxima a vencer en los próximos 15 días.\n");
        } else {
            sb.append(String.format("%-10s %-25s %-20s %-15s %s\n",
                    "ID", "Nombre", "Carrera", "Vencimiento", "Días Restantes"));
            sb.append("=".repeat(85)).append("\n");

            LocalDate today = LocalDate.now();
            for (Student s : expiring) {
                long daysLeft = s.getArlExpirationDate() != null ?
                        java.time.temporal.ChronoUnit.DAYS.between(today, s.getArlExpirationDate()) : 0;
                sb.append(String.format("%-10s %-25s %-20s %-15s %d\n",
                        s.getId(),
                        truncate(s.getFullName(), 24),
                        truncate(s.getCareer(), 19),
                        s.getArlExpirationDate() != null ?
                                s.getArlExpirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                        daysLeft));
            }
        }

        sb.append("\nTotal: ").append(expiring.size()).append(" estudiantes con ARL por vencer\n");
        return sb.toString();
    }

    private List<LocalDate> getEntryDates(Student student, LocalDate start, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (date.getDayOfWeek().getValue() <= 5) {
                dates.add(date);
            }
        }
        return dates;
    }

    private String getExitTimeForDate(Student student, LocalDate date) {
        LocalTime start = getShiftStartHour(student.getShift());
        return start.plusHours(8).format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private int calculateHoursCompleted(Shift shift) {
        LocalTime start = getShiftStartHour(shift);
        LocalTime end = getShiftEndHour(shift);
        return (int) java.time.Duration.between(start, end).toHours();
    }

    private LocalTime getShiftStartHour(Shift shift) {
        if (shift == null) return LocalTime.of(8, 0);
        switch (shift) {
            case MANANA: return LocalTime.of(6, 0);
            case TARDE: return LocalTime.of(12, 0);
            case NOCHE: return LocalTime.of(18, 0);
            default: return LocalTime.of(8, 0);
        }
    }

    private LocalTime getShiftEndHour(Shift shift) {
        if (shift == null) return LocalTime.of(16, 0);
        switch (shift) {
            case MANANA: return LocalTime.of(14, 0);
            case TARDE: return LocalTime.of(20, 0);
            case NOCHE: return LocalTime.of(23, 59);
            default: return LocalTime.of(16, 0);
        }
    }

    public List<Student> getStudentsWithExpiringARL(int days) {
        List<Student> result = new ArrayList<>();
        List<Student> allStudents = studentService.getAllStudents();
        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(days);

        for (Student student : allStudents) {
            if (student.getArlExpirationDate() != null) {
                if (!student.getArlExpirationDate().isAfter(limitDate) &&
                        !student.getArlExpirationDate().isBefore(today)) {
                    result.add(student);
                }
            }
        }
        return result;
    }

    public Map<String, Integer> getOccupancyByService() {
        Map<String, Integer> occupancy = new LinkedHashMap<>();
        occupancy.put("Urgencias", 15);
        occupancy.put("Consulta Externa", 25);
        occupancy.put("Hospitalización", 30);
        occupancy.put("Quirófano", 10);
        occupancy.put("Pediatría", 8);
        occupancy.put("Medicina Interna", 12);
        List<Student> students = studentService.getAllStudents();
        int extra = students.size() / 2;
        occupancy.put("Rotación de Estudiantes", extra);
        return occupancy;
    }

    public void exportToExcel(String reportContent, String reportType, String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename);
             Workbook workbook = new XSSFWorkbook()) {

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Reporte");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] lines = reportContent.split("\n");
            int rowIdx = 0;

            for (String line : lines) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                if (line.startsWith("===")) {
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(0);
                    cell.setCellValue(line.replace("=", "").trim());
                    cell.setCellStyle(headerStyle);
                } else if (line.contains("|")) {
                    String[] parts = line.split("\\|");
                    for (int i = 0; i < parts.length; i++) {
                        row.createCell(i).setCellValue(parts[i].trim());
                    }
                } else if (line.startsWith("=")) {
                    continue;
                } else {
                    row.createCell(0).setCellValue(line);
                }
            }

            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportToPdf(String reportContent, String reportType, String filename) {
        try {
            PdfWriter writer = new PdfWriter(filename);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph(reportType)
                    .setBold()
                    .setFontSize(18));

            document.add(new Paragraph("Hospital San Rafael - Sistema de Gestión")
                    .setFontSize(12));
            document.add(new Paragraph("Fecha de generación: " + LocalDate.now())
                    .setFontSize(10));

            document.add(new Paragraph("\n"));

            String[] lines = reportContent.split("\n");
            Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();

            for (String line : lines) {
                if (line.startsWith("===")) {
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (line.contains("|")) {
                    String[] parts = line.split("\\|");
                    for (String part : parts) {
                        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(part.trim())));
                    }
                } else {
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(line)));
                }
            }

            document.add(table);
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportToExcel(List<Student> students, String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename);
             Workbook workbook = new XSSFWorkbook()) {

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Estudiantes");

            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Nombre");
            header.createCell(2).setCellValue("Carrera");
            header.createCell(3).setCellValue("Semestre");
            header.createCell(4).setCellValue("Email");

            int rowIdx = 1;
            for (Student s : students) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getFullName());
                row.createCell(2).setCellValue(s.getCareer());
                row.createCell(3).setCellValue(s.getSemester());
                row.createCell(4).setCellValue(s.getEmail());
            }

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportToPdf(List<Student> students, String filename) {
        exportToPdf(generateEntryExitHistory(LocalDate.now().minusMonths(1), LocalDate.now()),
                "Reporte de Estudiantes", filename);
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 1) + "…";
    }
}
