package com.hospital.sanrafael.service;

import com.hospital.sanrafael.model.DataChangeRequest;
import com.hospital.sanrafael.model.DataChangeRequest.Status;
import com.hospital.sanrafael.model.Doctor;
import com.hospital.sanrafael.model.Notification;
import com.hospital.sanrafael.model.Student;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataChangeRequestService {
    private static final String FILE_PATH = "data" + File.separator + "solicitudes.dat";
    private static DataChangeRequestService instance;
    private final List<DataChangeRequest> requests;
    private final NotificationService notificationService;
    private final StudentService studentService;
    private final DoctorService doctorService;

    public DataChangeRequestService() {
        this.requests = new CopyOnWriteArrayList<>(loadFromFile());
        this.notificationService = NotificationService.getInstance();
        this.studentService = new StudentService();
        this.doctorService = new DoctorService();
    }

    public static synchronized DataChangeRequestService getInstance() {
        if (instance == null) {
            instance = new DataChangeRequestService();
        }
        return instance;
    }

    public void submitStudentChange(Student original, Map<String, String> proposedChanges) {
        DataChangeRequest req = new DataChangeRequest();
        req.setId(generateId());
        req.setRequesterId(original.getId());
        req.setRequesterName(original.getFullName());
        req.setRequesterRole("Estudiante");
        req.setEntityId(original.getId());
        req.setEntityType("Student");

        Map<String, String> originalData = new LinkedHashMap<>();
        originalData.put("Nombres", original.getFirstName());
        originalData.put("Apellidos", original.getLastName());
        originalData.put("Email", original.getEmail());
        originalData.put("Telefono", original.getPhone());
        originalData.put("Fecha Nacimiento", original.getBirthDate());
        originalData.put("Genero", original.getGender());
        originalData.put("Direccion", original.getAddress());
        originalData.put("Carrera", original.getCareer());
        originalData.put("Semestre", String.valueOf(original.getSemester()));
        originalData.put("Turno", original.getShift() != null ? original.getShift().getDisplayName() : "");
        req.setOriginalData(originalData);

        req.setProposedData(new LinkedHashMap<>(proposedChanges));

        requests.add(0, req);
        saveToFile();

        notificationService.addNotification(
            "Nueva solicitud de cambio de datos",
            String.format("El estudiante %s (%s) solicita modificar sus datos personales.",
                original.getFullName(), original.getId()),
            Notification.Type.INFO,
            Notification.Category.GENERAL,
            "admin",
            original.getFullName()
        );
    }

    public void submitDoctorChange(Doctor original, Map<String, String> proposedChanges) {
        DataChangeRequest req = new DataChangeRequest();
        req.setId(generateId());
        req.setRequesterId(original.getId());
        req.setRequesterName(original.getFullName());
        req.setRequesterRole("Doctor");
        req.setEntityId(original.getId());
        req.setEntityType("Doctor");

        Map<String, String> originalData = new LinkedHashMap<>();
        originalData.put("Nombres", original.getFirstName());
        originalData.put("Apellidos", original.getLastName());
        originalData.put("Email", original.getEmail());
        originalData.put("Telefono", original.getPhone());
        originalData.put("Fecha Nacimiento", original.getBirthDate());
        originalData.put("Genero", original.getGender());
        originalData.put("Direccion", original.getAddress());
        originalData.put("Especialidad", original.getSpecialty());
        originalData.put("N Colegiado", original.getLicenseNumber());
        originalData.put("Area", original.getAssignedArea());
        originalData.put("Anios Experiencia", String.valueOf(original.getYearsExperience()));
        req.setOriginalData(originalData);

        req.setProposedData(new LinkedHashMap<>(proposedChanges));

        requests.add(0, req);
        saveToFile();

        notificationService.addNotification(
            "Nueva solicitud de cambio de datos",
            String.format("El doctor %s (%s) solicita modificar sus datos personales.",
                original.getFullName(), original.getId()),
            Notification.Type.INFO,
            Notification.Category.GENERAL,
            "admin",
            original.getFullName()
        );
    }

    public void approveRequest(String requestId, String adminMessage) {
        DataChangeRequest req = findById(requestId);
        if (req == null || !req.isPending()) return;

        req.setStatus(Status.APPROVED);
        req.setAdminMessage(adminMessage);
        req.setResponseDate(LocalDate.now());
        applyChanges(req);
        saveToFile();

        notificationService.addNotification(
            "Solicitud de cambio APROBADA",
            String.format("Administrador aprobo tu solicitud de cambio de datos. %s",
                adminMessage != null && !adminMessage.isEmpty() ? "Mensaje: " + adminMessage : ""),
            Notification.Type.SUCCESS,
            Notification.Category.GENERAL,
            req.getRequesterId(),
            req.getRequesterName()
        );
    }

    public void denyRequest(String requestId, String reason) {
        DataChangeRequest req = findById(requestId);
        if (req == null || !req.isPending()) return;

        req.setStatus(Status.DENIED);
        req.setAdminMessage(reason);
        req.setResponseDate(LocalDate.now());
        saveToFile();

        notificationService.addNotification(
            "Solicitud de cambio DENEGADA",
            "Motivo: " + (reason != null && !reason.isEmpty() ? reason : "No se especifico motivo"),
            Notification.Type.ERROR,
            Notification.Category.GENERAL,
            req.getRequesterId(),
            req.getRequesterName()
        );
    }

    public List<DataChangeRequest> getAllRequests() {
        return new ArrayList<>(requests);
    }

    public List<DataChangeRequest> getPendingRequests() {
        return requests.stream().filter(DataChangeRequest::isPending).toList();
    }

    public List<DataChangeRequest> getRequestsByRequester(String requesterId) {
        return requests.stream()
                .filter(r -> requesterId.equals(r.getRequesterId()))
                .toList();
    }

    public DataChangeRequest findById(String id) {
        return requests.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void applyChanges(DataChangeRequest req) {
        try {
            if ("Student".equals(req.getEntityType())) {
                Student student = studentService.getStudentById(req.getEntityId());
                if (student != null) {
                    Map<String, String> prop = req.getProposedData();
                    if (prop.containsKey("Nombres")) student.setFirstName(prop.get("Nombres"));
                    if (prop.containsKey("Apellidos")) student.setLastName(prop.get("Apellidos"));
                    if (prop.containsKey("Email")) student.setEmail(prop.get("Email"));
                    if (prop.containsKey("Telefono")) student.setPhone(prop.get("Telefono"));
                    if (prop.containsKey("Fecha Nacimiento")) student.setBirthDate(prop.get("Fecha Nacimiento"));
                    if (prop.containsKey("Genero")) student.setGender(prop.get("Genero"));
                    if (prop.containsKey("Direccion")) student.setAddress(prop.get("Direccion"));
                    if (prop.containsKey("Carrera")) student.setCareer(prop.get("Carrera"));
                    if (prop.containsKey("Semestre")) {
                        try { student.setSemester(Integer.parseInt(prop.get("Semestre"))); } catch (NumberFormatException e) {}
                    }
                    studentService.updateStudent(student);
                }
            } else if ("Doctor".equals(req.getEntityType())) {
                Doctor doctor = doctorService.getDoctorById(req.getEntityId());
                if (doctor != null) {
                    Map<String, String> prop = req.getProposedData();
                    if (prop.containsKey("Nombres")) doctor.setFirstName(prop.get("Nombres"));
                    if (prop.containsKey("Apellidos")) doctor.setLastName(prop.get("Apellidos"));
                    if (prop.containsKey("Email")) doctor.setEmail(prop.get("Email"));
                    if (prop.containsKey("Telefono")) doctor.setPhone(prop.get("Telefono"));
                    if (prop.containsKey("Fecha Nacimiento")) doctor.setBirthDate(prop.get("Fecha Nacimiento"));
                    if (prop.containsKey("Genero")) doctor.setGender(prop.get("Genero"));
                    if (prop.containsKey("Direccion")) doctor.setAddress(prop.get("Direccion"));
                    if (prop.containsKey("Especialidad")) doctor.setSpecialty(prop.get("Especialidad"));
                    if (prop.containsKey("N Colegiado")) doctor.setLicenseNumber(prop.get("N Colegiado"));
                    if (prop.containsKey("Area")) doctor.setAssignedArea(prop.get("Area"));
                    if (prop.containsKey("Anios Experiencia")) {
                        try { doctor.setYearsExperience(Integer.parseInt(prop.get("Anios Experiencia"))); } catch (NumberFormatException e) {}
                    }
                    doctorService.updateDoctor(doctor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateId() {
        return "SOL" + System.currentTimeMillis();
    }

    private void saveToFile() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) dataDir.mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
                oos.writeObject(new ArrayList<>(requests));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private List<DataChangeRequest> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<DataChangeRequest>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
