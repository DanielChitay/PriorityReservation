/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.dto;

import com.example.priorityreservation.model.Task;
import java.time.LocalDateTime;

/**
 *
 * @author rodol
 */
public record TaskInfoDTO(Long id, String title, Task.TaskStatus status, LocalDateTime createdAt) {
    public static TaskInfoDTO fromEntity(Task task) {
        if (task == null) {
            return null;
        }
        return new TaskInfoDTO(
            task.getId(), 
            task.getTitle(), 
            task.getStatus(),
            task.getCreatedAt()
        );
    }
}