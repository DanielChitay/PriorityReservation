package com.example.priorityreservation.dto;

import jakarta.validation.constraints.*;

public class TaskRequestDTO {
    @NotBlank(message = "Title is mandatory")
    private String title;
    
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    @Pattern(regexp = "PENDING|IN_PROGRESS|COMPLETED", message = "Status must be PENDING, IN_PROGRESS or COMPLETED")
    private String status = "PENDING"; // Valor por defecto
    
    @NotNull(message = "User ID is mandatory")
    private Long assignedUserId; // Cambiado de userId a assignedUserId para coincidir con la entidad
    
    @NotBlank(message = "Priority is mandatory")
    @Pattern(regexp = "HIGH|MEDIUM|LOW", message = "Priority must be HIGH, MEDIUM or LOW")
    private String priority;


}