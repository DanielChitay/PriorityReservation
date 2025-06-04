/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.dto;

import com.example.priorityreservation.model.User;
import java.time.LocalDateTime;

/**
 *
 * @author rodol
 */
public record UserResponseDTO(
    Long id,
    String username,
    String email,
    LocalDateTime createdAt
) {
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt()
        );
    }
}