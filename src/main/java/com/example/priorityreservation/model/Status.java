/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.model;

/**
 *
 * @author rodol
 */




public enum Status {
    PENDING,
    IN_PROGRESS,
    COMPLETED;

    // Método para evitar problemas con mayúsculas/minúsculas
    public static Status fromString(String value) {
        try {
            return Status.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido. Use: PENDING, IN_PROGRESS o COMPLETED");
        }
    }
}