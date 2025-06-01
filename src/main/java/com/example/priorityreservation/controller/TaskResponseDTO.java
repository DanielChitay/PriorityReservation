package com.example.priorityreservation.controller;

import com.example.priorityreservation.model.Task;
import java.time.LocalDateTime;

public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserInfoDTO assignedUser;

    // Getters y Setters del DTO principal
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public UserInfoDTO getAssignedUser() { return assignedUser; }
    public void setAssignedUser(UserInfoDTO assignedUser) { this.assignedUser = assignedUser; }

    // Clase interna para el DTO de User
    public static class UserInfoDTO {
        private Long id;
        private String username;
        private String email;
        
        // Getters y Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    // Método de conversión mejorado
    public static TaskResponseDTO fromEntity(Task task) {
        if (task == null) return null;
        
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        
        if (task.getPriority() != null) {
            dto.setPriority(task.getPriority().toString());
        }
        
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        
        if (task.getAssignedUser() != null) {
            UserInfoDTO userDto = new UserInfoDTO();
            userDto.setId(task.getAssignedUser().getId());
            userDto.setUsername(task.getAssignedUser().getUsername());
            userDto.setEmail(task.getAssignedUser().getEmail());
            dto.setAssignedUser(userDto);
        }
        
        return dto;
    }
}