package com.hospital.sanrafael.dao;

import com.hospital.sanrafael.model.Student;
import com.hospital.sanrafael.service.EncryptionUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private final String filePath = "data" + File.separator + "estudiantes.dat";

    public StudentDAO() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    public List<Student> getAll() {
        List<Student> list = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(EncryptionUtils.decryptStream(new FileInputStream(filePath)))) {
            while (true) {
                try {
                    Student obj = (Student) ois.readObject();
                    list.add(obj);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
        }
        return list;
    }

    public void save(Student student) {
        List<Student> list = getAll();
        list.add(student);
        try (ObjectOutputStream oos = new ObjectOutputStream(EncryptionUtils.encryptStream(new FileOutputStream(filePath)))) {
            for (Student e : list) {
                oos.writeObject(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(Student student) {
        List<Student> list = getAll();
        List<Student> updated = new ArrayList<>();
        for (Student s : list) {
            if (s.getId().equals(student.getId())) {
                updated.add(student);
            } else {
                updated.add(s);
            }
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(EncryptionUtils.encryptStream(new FileOutputStream(filePath)))) {
            for (Student e : updated) {
                oos.writeObject(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(String id) {
        List<Student> list = getAll();
        list.removeIf(e -> e.getId().equals(id));
        try (ObjectOutputStream oos = new ObjectOutputStream(EncryptionUtils.encryptStream(new FileOutputStream(filePath)))) {
            for (Student e : list) {
                oos.writeObject(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Student getById(String id) {
        return getAll().stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Student> getByCareer(String career) {
        return getAll().stream()
                .filter(e -> e.getCareer().equals(career))
                .toList();
    }

    public List<Student> getBySemester(int semester) {
        return getAll().stream()
                .filter(e -> e.getSemester() == semester)
                .toList();
    }
}
