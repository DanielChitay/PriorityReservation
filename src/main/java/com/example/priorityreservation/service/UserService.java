/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.service;

import com.example.priorityreservation.dto.TaskResponseDTO;
import com.example.priorityreservation.dto.UserRequestDTO;
import com.example.priorityreservation.dto.UserResponseDTO;
import com.example.priorityreservation.exception.ResourceNotFoundException;
import com.example.priorityreservation.model.Task;
import com.example.priorityreservation.model.User;
import com.example.priorityreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author rodol
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO createUser(UserRequestDTO userRequest) {
        User user = new User();
        user.setUsername(userRequest.username());
        user.setEmail(userRequest.email());
        
        User savedUser = userRepository.save(user);
        return UserResponseDTO.fromEntity(savedUser);
    }
    
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponseDTO.fromEntity(user);
    }
    

}