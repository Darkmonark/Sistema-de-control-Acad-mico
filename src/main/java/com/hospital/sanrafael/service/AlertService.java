package com.hospital.sanrafael.service;

import com.hospital.sanrafael.model.Doctor;
import com.hospital.sanrafael.model.Notification;
import com.hospital.sanrafael.model.Notification.Category;
import com.hospital.sanrafael.model.Notification.Type;
import com.hospital.sanrafael.model.Schedule;
import com.hospital.sanrafael.model.Shift;
import com.hospital.sanrafael.model.Student;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertService {
    private static AlertService instance;
    private final NotificationService notificationService;
    private final Map<String, LocalDateTime> entryTimes;
    private final Map<String, LocalTime> maxExitTimes;
    private final int maxHoursInHospital = 8;

    private AlertService() {
        notificationService = NotificationService.getInstance();
        entryTimes = new HashMap<>();
        maxExitTimes = new HashMap<>();
    }

    public static synchronized AlertService getInstance() {
        if (instance == null) {
            instance = new AlertService();
        }
        return instance;
    }

    public boolean validateStudentEntry(Student student, Doctor assignedDoctor) {
        boolean canEnter = true;

        if (assignedDoctor == null || student.getAssignedDoctorId() == null) {
            notificationService.addNotification(
                "Estudiante sin docente asignado",
                String.format("El estudiante %s (%s) intentó ingresar sin un docente responsable registrado.",
                    student.getFullName(), student.getId()),
                Type.ERROR,
                Category.ACCESS_DENIED,
                student.getId(),
                student.getFullName()
            );
            canEnter = false;
        }

        if (!isWithinSchedule(student)) {
            notificationService.addNotification(
                "Fuera de horario permitido",
                String.format("El estudiante %s (%s) intentó ingresar fuera de su franja horaria (%s).",
                    student.getFullName(), student.getId(), student.getShift().getDisplayName()),
                Type.ERROR,
                Category.SCHEDULE_VIOLATION,
                student.getId(),
                student.getFullName()
            );
            canEnter = false;
        }

        if (canEnter) {
            registerEntry(student);
        }

        return canEnter;
    }

    public void checkArlExpirations(List<Student> students) {
        LocalDate today = LocalDate.now();
        LocalDate warningDate = today.plusDays(15);

        for (Student student : students) {
            if (student.getArlExpirationDate() != null) {
                if (student.getArlExpirationDate().isBefore(today)) {
                    notificationService.addNotification(
                        "ARL Vencida",
                        String.format("La ARL del estudiante %s (%s) está vencida desde %s.",
                            student.getFullName(),
                            student.getId(),
                            student.getArlExpirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        ),
                        Type.ERROR,
                        Category.INSURANCE_EXPIRY,
                        student.getId(),
                        student.getFullName()
                    );
                } else if (!student.getArlExpirationDate().isAfter(warningDate)) {
                    notificationService.addNotification(
                        "ARL por Vencer",
                        String.format("La ARL del estudiante %s (%s) vencerá en %s días (%s).",
                            student.getFullName(),
                            student.getId(),
                            Duration.between(today.atStartOfDay(), student.getArlExpirationDate().atStartOfDay()).toDays() + 1,
                            student.getArlExpirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        ),
                        Type.WARNING,
                        Category.INSURANCE_EXPIRY,
                        student.getId(),
                        student.getFullName()
                    );
                }
            }
        }
    }

    public void registerExit(Student student) {
        String studentId = student.getId();
        if (entryTimes.containsKey(studentId)) {
            entryTimes.remove(studentId);
        }

        LocalTime currentTime = LocalTime.now();
        LocalTime maxExitTime = maxExitTimes.get(studentId);
        
        if (maxExitTime != null && currentTime.isAfter(maxExitTime)) {
            notificationService.addNotification(
                "Tiempo excedido",
                String.format("El estudiante %s (%s) superó el tiempo máximo permitido en el hospital. Hora salida: %s, Hora límite: %s.",
                    student.getFullName(),
                    student.getId(),
                    currentTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    maxExitTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                ),
                Type.WARNING,
                Category.TIME_EXCEEDED,
                student.getId(),
                student.getFullName()
            );
        }

        if (maxExitTimes.containsKey(studentId)) {
            maxExitTimes.remove(studentId);
        }
    }

    private boolean isWithinSchedule(Student student) {
        Shift shift = student.getShift();
        if (shift == null) return false;

        LocalTime now = LocalTime.now();
        LocalTime startHour = getShiftStartHour(shift);
        LocalTime endHour = getShiftEndHour(shift);

        return !now.isBefore(startHour.minusHours(1)) && !now.isAfter(endHour.plusHours(1));
    }

    private LocalTime getShiftStartHour(Shift shift) {
        switch (shift) {
            case MANANA: return LocalTime.of(6, 0);
            case TARDE: return LocalTime.of(12, 0);
            case NOCHE: return LocalTime.of(18, 0);
            default: return LocalTime.of(8, 0);
        }
    }

    private LocalTime getShiftEndHour(Shift shift) {
        switch (shift) {
            case MANANA: return LocalTime.of(14, 0);
            case TARDE: return LocalTime.of(20, 0);
            case NOCHE: return LocalTime.of(23, 59);
            default: return LocalTime.of(16, 0);
        }
    }

    private void registerEntry(Student student) {
        String studentId = student.getId();
        LocalDateTime now = LocalDateTime.now();
        entryTimes.put(studentId, now);

        LocalTime maxExit = calculateMaxExitTime(student.getShift());
        maxExitTimes.put(studentId, maxExit);
    }

    private LocalTime calculateMaxExitTime(Shift shift) {
        LocalTime startHour = getShiftStartHour(shift);
        return startHour.plusHours(maxHoursInHospital);
    }

    public LocalDateTime getEntryTime(String studentId) {
        return entryTimes.get(studentId);
    }

    public LocalTime getMaxExitTime(String studentId) {
        return maxExitTimes.get(studentId);
    }
}
