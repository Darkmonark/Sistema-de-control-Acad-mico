package com.hospital.sanrafael.dao;

import com.hospital.sanrafael.model.Subject;
import com.hospital.sanrafael.service.EncryptionUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {
    private final String filePath = "data" + File.separator + "materias.dat";

    public SubjectDAO() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    public List<Subject> getAll() {
        List<Subject> list = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(EncryptionUtils.decryptStream(new FileInputStream(filePath)))) {
            while (true) {
                try {
                    Subject obj = (Subject) ois.readObject();
                    list.add(obj);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
        }
        return list;
    }

    public void save(Subject subject) {
        List<Subject> list = getAll();
        list.add(subject);
        try (ObjectOutputStream oos = new ObjectOutputStream(EncryptionUtils.encryptStream(new FileOutputStream(filePath)))) {
            for (Subject m : list) {
                oos.writeObject(m);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(Subject subject) {
        List<Subject> list = getAll();
        List<Subject> updated = new ArrayList<>();
        for (Subject m : list) {
            if (m.getCode().equals(subject.getCode())) {
                updated.add(subject);
            } else {
                updated.add(m);
            }
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(EncryptionUtils.encryptStream(new FileOutputStream(filePath)))) {
            for (Subject m : updated) {
                oos.writeObject(m);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(String code) {
        List<Subject> list = getAll();
        list.removeIf(m -> m.getCode().equals(code));
        try (ObjectOutputStream oos = new ObjectOutputStream(EncryptionUtils.encryptStream(new FileOutputStream(filePath)))) {
            for (Subject m : list) {
                oos.writeObject(m);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Subject getByCode(String code) {
        return getAll().stream()
                .filter(m -> m.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public List<Subject> getBySemester(int semester) {
        return getAll().stream()
                .filter(m -> m.getRecommendedSemester() == semester)
                .toList();
    }

    public List<Subject> getByProfessor(String professor) {
        return getAll().stream()
                .filter(m -> m.getProfessor().equals(professor))
                .toList();
    }
}
