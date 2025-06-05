/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.service;

import com.example.priorityreservation.model.Task;
import com.example.priorityreservation.model.Task.TaskStatus;
import com.example.priorityreservation.model.TaskHistory;
import com.example.priorityreservation.repository.TaskHistoryRepository;
import com.example.priorityreservation.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author rodol
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TaskAuditService {
    private final TaskHistoryRepository taskHistoryRepository;
    private final TaskRepository taskRepository;
    
    public Optional<TaskHistory> getLastStatusChange(Long taskId) {
        return taskHistoryRepository.findLastStatusChangeNative(taskId); 
 
    }
   
    public void recordTaskChange(Long taskId, String fieldName, 
                               String oldValue, String newValue) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));
        
        TaskHistory history = TaskHistory.builder()
            .task(task)
            .fieldName(fieldName)
            .oldValue(oldValue)
            .newValue(newValue)
            .changedBy(getCurrentUsername())
            .build();
        
        taskHistoryRepository.save(history);
    }
    
    public void recordStatusChange(Task task, TaskStatus oldStatus, TaskStatus newStatus) {
        recordTaskChange(task.getId(), "status", 
                       oldStatus != null ? oldStatus.name() : null, 
                       newStatus.name());
    }
    
    private String getCurrentUsername() {
        return "DANIEL_VELASQUEZ";
    }
}