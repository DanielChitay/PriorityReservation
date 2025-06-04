/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author rodol
 */
@Entity
@Table(name = "action_stack")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType entityType;

    @Column(nullable = false)
    private Long entityId;

    @Column(columnDefinition = "JSON")
    private String entityData;

    @CreationTimestamp
    @Column(name = "performed_at", updatable = false)
    private LocalDateTime performedAt;

    public enum ActionType {
        CREATE, UPDATE, DELETE, STATUS_CHANGE
    }

    public enum EntityType {
        TASK, USER
    }
}