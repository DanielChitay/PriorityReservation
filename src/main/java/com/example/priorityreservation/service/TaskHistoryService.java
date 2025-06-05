/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.service;

import com.example.priorityreservation.model.Task;
import com.example.priorityreservation.model.TaskHistory;
import com.example.priorityreservation.repository.TaskHistoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author rodol
 */
@Service
@Transactional
public class TaskHistoryService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private TaskHistoryRepository taskHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordHistory(Task task, String fieldName, String oldValue, String newValue, String changedBy) {
        try {
            Task managedTask = entityManager.find(Task.class, task.getId());
            if (managedTask == null) {
                managedTask = entityManager.merge(task);
            }

            TaskHistory history = new TaskHistory();
            history.setTask(managedTask);
            history.setFieldName(fieldName);
            history.setOldValue(oldValue);
            history.setNewValue(newValue);
            history.setChangedAt(LocalDateTime.now());
            history.setChangedBy(changedBy);

            taskHistoryRepository.save(history);
        } catch (Exception e) {
               System.out.println("Error:" + e.toString());
          
        }
    }

    @Transactional(readOnly = true)
    public Task getCurrentTaskState(Long taskId) {
        return entityManager.find(Task.class, taskId);
    }
}