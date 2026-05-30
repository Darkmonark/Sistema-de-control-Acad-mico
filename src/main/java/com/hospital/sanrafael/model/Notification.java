package com.hospital.sanrafael.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        WARNING,
        ERROR,
        INFO,
        SUCCESS
    }

    public enum Category {
        ACCESS_DENIED,
        SCHEDULE_VIOLATION,
        INSURANCE_EXPIRY,
        TIME_EXCEEDED,
        GENERAL
    }

    private String id;
    private String message;
    private String details;
    private Type type;
    private Category category;
    private LocalDate date;
    private boolean read;
    private String personId;
    private String personName;

    public Notification() {
        this.date = LocalDate.now();
        this.read = false;
        this.type = Type.INFO;
        this.category = Category.GENERAL;
    }

    public Notification(String message, Type type, Category category) {
        this();
        this.message = message;
        this.type = type;
        this.category = category;
    }

    public Notification(String message, String details, Type type, Category category) {
        this(message, type, category);
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", type, category, message);
    }
}
