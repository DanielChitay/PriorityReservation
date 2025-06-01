/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.repository;

import com.example.priorityreservation.model.Task;
import com.example.priorityreservation.model.Priority;
import com.example.priorityreservation.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author rodol
 */


public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedUserId(Long userId);
    
    @Query("SELECT t FROM Task t WHERE t.priority = :priority")
    List<Task> findByPriority(@Param("priority") Priority priority);

    List<Task> findByTitleContaining(String title);
    List<Task> findByTitleContainingAndPriority(String title, Priority priority);
    List<Task> findByTitleContainingAndStatus(String title, Status status);
    List<Task> findByTitleContainingAndPriorityAndStatus(String title, Priority priority, Status status);
    List<Task> findByPriorityAndStatus(Priority priority, Status status);
    List<Task> findByStatus(Status status);

    @Override
    Optional<Task> findById(Long id);

}