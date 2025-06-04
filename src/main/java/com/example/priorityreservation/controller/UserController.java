/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.controller;

import com.example.priorityreservation.dto.TaskResponseDTO;
import com.example.priorityreservation.dto.UserRequestDTO;
import com.example.priorityreservation.dto.UserResponseDTO;
import com.example.priorityreservation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author rodol
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user")
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO userRequest) {
        return userService.createUser(userRequest);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    public UserResponseDTO getTask(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    
    
}

