package com.example.priorityreservation.dto;

import com.example.priorityreservation.model.Task;
import com.example.priorityreservation.model.TaskHistory;
import com.example.priorityreservation.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record TaskResponseDTO(
    Long id,
    String title,
    String description,
    Task.TaskStatus status,
    Task.TaskPriority priority,
    UserInfoDTO assignedUser,
    TaskInfoDTO parentTask,
    List<TaskInfoDTO> subtasks,
    List<TaskHistoryDTO> history,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static TaskResponseDTO fromEntity(Task task) {
        if (task == null) {
            return null;
        }

        return new TaskResponseDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getPriority(),
            UserInfoDTO.fromEntity(task.getAssignedUser()),
            task.getParentTask() != null ? TaskInfoDTO.fromEntity(task.getParentTask()) : null,
            task.getSubtasks().stream()
                .map(TaskInfoDTO::fromEntity)
                .collect(Collectors.toList()),
            task.getHistory().stream()
                .map(TaskHistoryDTO::fromEntity)
                .collect(Collectors.toList()),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }
}
