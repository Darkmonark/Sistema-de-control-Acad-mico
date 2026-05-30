package com.hospital.sanrafael.dao;

import com.hospital.sanrafael.service.EncryptionUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GenericDAO<T extends Serializable> {
    private final String filePath;
    private final Class<T> clazz;

    public GenericDAO(String filePath, Class<T> clazz) {
        this.filePath = filePath;
        this.clazz = clazz;
    }

    public List<T> getAll() {
        List<T> list = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(EncryptionUtils.decryptStream(new FileInputStream(filePath)))) {
            while (true) {
                try {
                    @SuppressWarnings("unchecked")
                    T obj = (T) ois.readObject();
                    list.add(obj);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
        }
        return list;
    }

    public void save(T entity) {
        List<T> list = getAll();
        list.add(entity);
        saveAll(list);
    }

    public void saveAll(List<T> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(EncryptionUtils.encryptStream(new FileOutputStream(filePath)))) {
            for (T entity : list) {
                oos.writeObject(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(T entity, Predicate<T> condition) {
        List<T> list = getAll();
        List<T> updated = new ArrayList<>();
        for (T item : list) {
            if (condition.test(item)) {
                updated.add(entity);
            } else {
                updated.add(item);
            }
        }
        saveAll(updated);
    }

    public void delete(Predicate<T> condition) {
        List<T> list = getAll();
        list.removeIf(condition);
        saveAll(list);
    }

    public T find(Predicate<T> condition) {
        return getAll().stream().filter(condition).findFirst().orElse(null);
    }

    public List<T> findAll(Predicate<T> condition) {
        return getAll().stream().filter(condition).toList();
    }

    public void clear() {
        saveAll(new ArrayList<>());
    }
}
