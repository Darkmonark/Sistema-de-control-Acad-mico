package com.hospital.sanrafael.model;

public enum Shift {
    MANANA("Mañana"),
    TARDE("Tarde"),
    NOCHE("Noche");

    private final String displayName;

    Shift(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Shift fromDisplayName(String name) {
        for (Shift s : values()) {
            if (s.displayName.equals(name)) return s;
        }
        return MANANA;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
