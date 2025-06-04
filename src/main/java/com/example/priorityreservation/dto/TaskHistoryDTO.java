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
    Long id,                // ID del registro de historial
    Long taskId,            // ID de la tarea relacionada
    String fieldName,       // Nombre del campo que cambió (ej. "status")
    String oldValue,        // Valor anterior del campo
    String newValue,        // Nuevo valor del campo
    LocalDateTime changedAt,// Fecha y hora del cambio
    String changedBy        // Usuario que realizó el cambio
) {
    /**
     * Método para convertir una entidad TaskHistory a su DTO correspondiente
     * @param history Entidad TaskHistory
     * @return TaskHistoryDTO o null si la entrada es null
     */
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