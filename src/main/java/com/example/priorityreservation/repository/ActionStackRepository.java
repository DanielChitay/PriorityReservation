/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.repository;

/**
 *
 * @author rodol
 */
import com.example.priorityreservation.model.ActionStack;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface ActionStackRepository extends JpaRepository<ActionStack, Long> {


    @Query(value = "SELECT * FROM action_stack ORDER BY performed_at DESC LIMIT 1", nativeQuery = true)
    Optional<ActionStack> findLatestAction();
    
 
    @Query(value = "SELECT * FROM action_stack WHERE entity_type = :entityType ORDER BY performed_at DESC LIMIT 1", nativeQuery = true)
    Optional<ActionStack> findLatestActionByEntityType(@Param("entityType") String entityType);
    
  
    @Query("SELECT a FROM ActionStack a WHERE a.entityType = 'TASK' AND a.entityId = :taskId ORDER BY a.performedAt DESC")
    List<ActionStack> findAllByTaskId(@Param("taskId") Long taskId);

    @Modifying
    @Query(nativeQuery = true, value = 
          "DELETE FROM action_stack WHERE id NOT IN " +
          "(SELECT id FROM action_stack ORDER BY performed_at DESC LIMIT :limit)")
    void keepOnlyRecentActions(@Param("limit") int limit);
    

    Optional<ActionStack> findTopByOrderByPerformedAtDesc();
    

    List<ActionStack> findByEntityTypeOrderByPerformedAtDesc(ActionStack.EntityType entityType);
    List<ActionStack> findByActionTypeAndEntityType(ActionStack.ActionType actionType, ActionStack.EntityType entityType);

}