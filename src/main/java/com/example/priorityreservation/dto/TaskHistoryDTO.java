/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.dto;

import com.example.priorityreservation.model.TaskHistory;
import java.time.LocalDateTime;

/**
 *
 * @author rodol
 */
public record TaskHistoryDTO(
    Long id,                
    Long taskId,            
    String fieldName,       
    String oldValue,        
    String newValue,        
    LocalDateTime changedAt,
    String changedBy      
) {

    public static TaskHistoryDTO fromEntity(TaskHistory history) {
        if (history == null) {
            return null;
        }
        
        return new TaskHistoryDTO(
            history.getId(),
            history.getTask() != null ? history.getTask().getId() : null,
            history.getFieldName(),
            history.getOldValue(),
            history.getNewValue(),
            history.getChangedAt(),
            history.getChangedBy()
        );
    }
}