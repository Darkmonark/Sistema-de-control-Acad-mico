package com.hospital.sanrafael.service;

import com.hospital.sanrafael.model.Notification;
import com.hospital.sanrafael.model.Notification.Category;
import com.hospital.sanrafael.model.Notification.Type;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationService {
    private static NotificationService instance;
    private final List<Notification> notifications;
    private final List<Notification> archivedNotifications;
    private final List<NotificationListener> listeners;
    private int unreadCount;

    private NotificationService() {
        notifications = new CopyOnWriteArrayList<>();
        archivedNotifications = new ArrayList<>();
        listeners = new ArrayList<>();
        unreadCount = 0;
    }

    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public void addNotification(String message, String details, Type type, Category category, String personId, String personName) {
        Notification notification = new Notification(message, details, type, category);
        notification.setPersonId(personId);
        notification.setPersonName(personName);
        notification.setId(generateId());
        notifications.add(0, notification);
        unreadCount++;
        notifyListeners(notification);
    }

    public void addNotification(String message, Type type, Category category) {
        addNotification(message, null, type, category, null, null);
    }

    public void addNotification(String message, String details, Type type, Category category) {
        addNotification(message, details, type, category, null, null);
    }

    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notifications);
    }

    public List<Notification> getUnreadNotifications() {
        List<Notification> unread = new ArrayList<>();
        for (Notification n : notifications) {
            if (!n.isRead()) {
                unread.add(n);
            }
        }
        return unread;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public int getUnreadCountForPerson(String personId) {
        int count = 0;
        for (Notification n : notifications) {
            if (!n.isRead() && personId.equals(n.getPersonId())) {
                count++;
            }
        }
        return count;
    }

    public int getUnreadCountForAdmin() {
        int count = 0;
        for (Notification n : notifications) {
            if (!n.isRead() && ("admin".equals(n.getPersonId()) || n.getPersonId() == null)) {
                count++;
            }
        }
        return count;
    }

    public List<Notification> getNotificationsByPerson(String personId) {
        List<Notification> result = new ArrayList<>();
        for (Notification n : notifications) {
            if (personId.equals(n.getPersonId())) {
                result.add(n);
            }
        }
        return result;
    }

    public List<Notification> getNotificationsByCategory(Category category) {
        List<Notification> result = new ArrayList<>();
        for (Notification n : notifications) {
            if (category == n.getCategory()) {
                result.add(n);
            }
        }
        return result;
    }

    public void markAsRead(String notificationId) {
        for (Notification n : notifications) {
            if (notificationId.equals(n.getId()) && !n.isRead()) {
                n.setRead(true);
                unreadCount--;
                break;
            }
        }
    }

    public void markAllAsRead() {
        for (Notification n : notifications) {
            if (!n.isRead()) {
                n.setRead(true);
            }
        }
        unreadCount = 0;
    }

    public void archiveNotification(String notificationId) {
        Notification toArchive = null;
        for (Notification n : notifications) {
            if (notificationId.equals(n.getId())) {
                toArchive = n;
                break;
            }
        }
        if (toArchive != null) {
            notifications.remove(toArchive);
            archivedNotifications.add(toArchive);
        }
    }

    public void archiveAll() {
        archivedNotifications.addAll(notifications);
        notifications.clear();
        unreadCount = 0;
    }

    public void deleteNotification(String notificationId) {
        notifications.removeIf(n -> notificationId.equals(n.getId()));
    }

    public void deleteAll() {
        notifications.clear();
        unreadCount = 0;
    }

    public void clearNotifications() {
        deleteAll();
    }

    public List<Notification> getArchivedNotifications() {
        return new ArrayList<>(archivedNotifications);
    }

    public void unarchiveNotification(String notificationId) {
        Notification toUnarchive = null;
        for (Notification n : archivedNotifications) {
            if (notificationId.equals(n.getId())) {
                toUnarchive = n;
                break;
            }
        }
        if (toUnarchive != null) {
            archivedNotifications.remove(toUnarchive);
            notifications.add(toUnarchive);
        }
    }

    public void exportToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== NOTIFICACIONES ===");
            writer.println("Fecha: " + LocalDate.now());
            writer.println("Total: " + notifications.size());
            writer.println("No leídas: " + unreadCount);
            writer.println("========================\n");
            
            for (Notification n : notifications) {
                writer.println("[" + n.getType() + "] " + n.getMessage());
                if (n.getDetails() != null) {
                    writer.println("  Detalle: " + n.getDetails());
                }
                writer.println("  Fecha: " + n.getDate());
                writer.println("  Persona: " + (n.getPersonName() != null ? n.getPersonName() : "N/A"));
                writer.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addListener(NotificationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Notification notification) {
        for (NotificationListener listener : listeners) {
            try {
                listener.onNotification(notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String generateId() {
        return "NOT" + System.currentTimeMillis();
    }

    public interface NotificationListener {
        void onNotification(Notification notification);
    }
}
