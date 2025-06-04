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

    /**
     * Encuentra la última acción realizada en cualquier entidad
     * @return Optional con la última acción o vacío si no hay acciones
     */
    @Query(value = "SELECT * FROM action_stack ORDER BY performed_at DESC LIMIT 1", nativeQuery = true)
    Optional<ActionStack> findLatestAction();
    
    /**
     * Encuentra la última acción realizada en un tipo específico de entidad
     * @param entityType Tipo de entidad (TASK o USER)
     * @return Optional con la última acción o vacío si no hay acciones
     */
    @Query(value = "SELECT * FROM action_stack WHERE entity_type = :entityType ORDER BY performed_at DESC LIMIT 1", nativeQuery = true)
    Optional<ActionStack> findLatestActionByEntityType(@Param("entityType") String entityType);
    
    /**
     * Encuentra todas las acciones relacionadas con una tarea específica
     * @param taskId ID de la tarea
     * @return Lista de acciones ordenadas por fecha descendente
     */
    @Query("SELECT a FROM ActionStack a WHERE a.entityType = 'TASK' AND a.entityId = :taskId ORDER BY a.performedAt DESC")
    List<ActionStack> findAllByTaskId(@Param("taskId") Long taskId);
    
    /**
     * Elimina acciones antiguas manteniendo solo las N más recientes
     * @param limit Número máximo de acciones a conservar
     */
    @Modifying
    @Query(nativeQuery = true, value = 
          "DELETE FROM action_stack WHERE id NOT IN " +
          "(SELECT id FROM action_stack ORDER BY performed_at DESC LIMIT :limit)")
    void keepOnlyRecentActions(@Param("limit") int limit);
    
     // Método para encontrar la última acción ordenada por timestamp


    
    // Cambia timestamp por performedAt
    Optional<ActionStack> findTopByOrderByPerformedAtDesc();
    
    // Métodos adicionales si los necesitas
    List<ActionStack> findByEntityTypeOrderByPerformedAtDesc(ActionStack.EntityType entityType);
    List<ActionStack> findByActionTypeAndEntityType(ActionStack.ActionType actionType, ActionStack.EntityType entityType);

}