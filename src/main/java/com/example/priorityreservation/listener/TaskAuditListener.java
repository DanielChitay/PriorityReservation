/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.listener;

import com.example.priorityreservation.model.Task;
import com.example.priorityreservation.service.TaskHistoryService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PreUpdate;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author rodol
 */
@Component
public class TaskAuditListener {
    
    private static TaskHistoryService taskHistoryService;

    @Autowired
    public void setTaskHistoryService(TaskHistoryService taskHistoryService) {
        TaskAuditListener.taskHistoryService = taskHistoryService;
    }

    @PostPersist
    public void afterCreate(Task task) {
        taskHistoryService.recordHistory(
            task,
            "TASK_CREATED",
            null,
            task.toString(),
            "SYSTEM"
        );
    }

    @PreUpdate
    public void beforeUpdate(Task task) {
        Task oldTask = taskHistoryService.getCurrentTaskState(task.getId());
        
        if (oldTask != null) {
            if (!Objects.equals(oldTask.getStatus(), task.getStatus())) {
                taskHistoryService.recordHistory(
                    task,
                    "STATUS_CHANGE",
                    oldTask.getStatus() != null ? oldTask.getStatus().name() : null,
                    task.getStatus() != null ? task.getStatus().name() : null,
                    getCurrentUser()
                );
            }
        }
    }

    private String getCurrentUser() {
        return  
               "DANIEL_VELASQUEZ";
    }
}