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

    public static Status fromString(String value) {
        if (value == null) {
            return PENDING;
        }
        try {
            return Status.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING; 
        }
    }
}