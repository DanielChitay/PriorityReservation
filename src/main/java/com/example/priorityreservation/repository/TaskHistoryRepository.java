/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.repository;
import com.example.priorityreservation.model.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;

/**
 *
 * @author rodol
 */


@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    
    // Versión corregida usando JPQL
    @Query("SELECT th FROM TaskHistory th " +
           "WHERE th.task.id = :taskId AND th.fieldName = 'status' " +
           "ORDER BY th.changedAt DESC")
    List<TaskHistory> findStatusChangesByTaskId(@Param("taskId") Long taskId);
    
    // Método alternativo para obtener el último cambio
    default Optional<TaskHistory> findLastStatusChange(Long taskId) {
        return findStatusChangesByTaskId(taskId)
            .stream()
            .findFirst();
    }
    
    // Otra opción usando consulta nativa
    @Query(value = "SELECT * FROM task_history th " +
                  "WHERE th.task_id = :taskId AND th.field_name = 'status' " +
                  "ORDER BY th.changed_at DESC LIMIT 1", 
           nativeQuery = true)
    Optional<TaskHistory> findLastStatusChangeNative(@Param("taskId") Long taskId);
}