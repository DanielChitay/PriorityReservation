/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 *
 * @author rodol
 */
public class TaskStatusUpdateDTO {
    @NotBlank
    @Pattern(regexp = "PENDING|IN_PROGRESS|COMPLETED", 
             message = "Status must be PENDING, IN_PROGRESS or COMPLETED")
    private String status;

    // Getters y Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}