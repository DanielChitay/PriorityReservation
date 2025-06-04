/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.dto;

import com.example.priorityreservation.model.User;

/**
 *
 * @author rodol
 */
public record UserInfoDTO(Long id, String username, String email) {
    public static UserInfoDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserInfoDTO(
            user.getId(), 
            user.getUsername(), 
            user.getEmail()
        );
    }
}