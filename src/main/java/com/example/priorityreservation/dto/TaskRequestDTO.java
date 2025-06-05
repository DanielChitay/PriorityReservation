package com.example.priorityreservation.dto;

import com.example.priorityreservation.model.Task;
import com.example.priorityreservation.model.Task.TaskPriority;
import com.example.priorityreservation.model.Task.TaskStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskRequestDTO {
    @NotBlank(message = "Title is mandatory")
    private String title;
    
    private String description;

    @NotNull(message = "Status cannot be null")
    private TaskStatus status = TaskStatus.PENDING;

    @NotNull(message = "Assigned user ID is mandatory")
    private Long assignedUserId;

    @NotNull(message = "Priority cannot be null")
    private TaskPriority priority = TaskPriority.MEDIUM;

    private Long parentTaskId;

    public TaskRequestDTO() {
    }

    public TaskRequestDTO(String title, String description, TaskStatus status, 
                         Long assignedUserId, TaskPriority priority, Long parentTaskId) {
        this.title = title;
        this.description = description;
        this.status = status != null ? status : TaskStatus.PENDING;
        this.assignedUserId = assignedUserId;
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
        this.parentTaskId = parentTaskId;
    }


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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status != null ? status : TaskStatus.PENDING;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
    }

    public Long getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(Long parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public Task toEntity() {
        Task task = new Task();
        task.setTitle(this.title);
        task.setDescription(this.description);
        task.setStatus(this.status);
        task.setPriority(this.priority);
        // Nota: assignedUser y parentTask se deben establecer en el servicio
        // después de verificar que existen en la base de datos
        return task;
    }

    // Patrón Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String description;
        private TaskStatus status = TaskStatus.PENDING;
        private Long assignedUserId;
        private TaskPriority priority = TaskPriority.MEDIUM;
        private Long parentTaskId;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder status(TaskStatus status) {
            this.status = status != null ? status : TaskStatus.PENDING;
            return this;
        }

        public Builder assignedUserId(Long assignedUserId) {
            this.assignedUserId = assignedUserId;
            return this;
        }

        public Builder priority(TaskPriority priority) {
            this.priority = priority != null ? priority : TaskPriority.MEDIUM;
            return this;
        }

        public Builder parentTaskId(Long parentTaskId) {
            this.parentTaskId = parentTaskId;
            return this;
        }

        public TaskRequestDTO build() {
            return new TaskRequestDTO(title, description, status, assignedUserId, priority, parentTaskId);
        }
    }
}