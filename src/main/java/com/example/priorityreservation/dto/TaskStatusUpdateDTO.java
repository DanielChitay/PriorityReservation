/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.dto;

import com.example.priorityreservation.model.Task;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author rodol
 */
public class TaskStatusUpdateDTO {
    @NotNull(message = "Status cannot be null")
    private Task.TaskStatus status;
    
    // Constructor
    public TaskStatusUpdateDTO() {}
    
    public TaskStatusUpdateDTO(Task.TaskStatus status) {
        this.status = status;
    }
    
    // Getter y Setter
    public Task.TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(Task.TaskStatus status) {
        this.status = status;
    }
}