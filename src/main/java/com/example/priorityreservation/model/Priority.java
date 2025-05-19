package com.example.priorityreservation.model;

public enum Priority {
    HIGH,
    MEDIUM,
    LOW;

    public static boolean isValid(String priority) {
        try {
            Priority.valueOf(priority.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}