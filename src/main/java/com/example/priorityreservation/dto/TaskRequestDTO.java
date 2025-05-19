package com.example.priorityreservation.dto;

import java.time.LocalDateTime;

public class TaskRequestDTO {
    private String title;
    private String description;
    private String priority; // Debe coincidir con los valores del enum Priority
    private LocalDateTime dueDate;
    private Long userId;

    // Getters y Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}