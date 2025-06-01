package com.example.priorityreservation.controller;

import com.example.priorityreservation.model.Task;
import com.example.priorityreservation.dto.TaskRequestDTO;
import com.example.priorityreservation.dto.TaskStatusUpdateDTO;
import com.example.priorityreservation.model.Priority;
import com.example.priorityreservation.model.Status;
import com.example.priorityreservation.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import org.springframework.validation.BindingResult;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Management", description = "APIs for task operations")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Get all tasks for a user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getUserTasks(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(taskService.getTasksByUser(userId));
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "User not found with id: " + userId, 
                e
            );
        }
    }

@Operation(summary = "Create a new task")
@PostMapping
public ResponseEntity<?> createTask(
    @Valid @RequestBody TaskRequestDTO taskDTO, 
    BindingResult bindingResult
) {
    if (bindingResult.hasErrors()) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
    
    try {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(taskService.createTask(taskDTO));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(
            Map.of("error", e.getMessage()));
    }
}
@Operation(summary = "Get task by ID")
@GetMapping("/{taskId}")
public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long taskId) {
    Task task = taskService.getTaskById(taskId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, 
            "Task not found with id: " + taskId
        ));
    
    return ResponseEntity.ok(TaskResponseDTO.fromEntity(task));
}
    @Operation(summary = "Mark task as completed")
    @PutMapping("/{taskId}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long taskId) {
        try {
            return ResponseEntity.ok(taskService.markTaskAsCompleted(taskId));
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Task not found with id: " + taskId,
                e
            );
        }
    }
    
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus(
        @PathVariable Long taskId,
        @Valid @RequestBody TaskStatusUpdateDTO statusUpdate) {
        
        Task updatedTask = taskService.updateTaskStatus(taskId, statusUpdate.getStatus());
        return ResponseEntity.ok(updatedTask);
    }

}