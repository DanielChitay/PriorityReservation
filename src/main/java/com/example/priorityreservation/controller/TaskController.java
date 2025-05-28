package com.example.priorityreservation.controller;

import com.example.priorityreservation.model.Task;
import com.example.priorityreservation.dto.TaskRequestDTO;
import com.example.priorityreservation.model.Priority;
import com.example.priorityreservation.model.Status;
import com.example.priorityreservation.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
    public ResponseEntity<Task> createTask(@RequestBody TaskRequestDTO taskDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(taskDTO));
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid task data",
                e
            );
        }
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
    
    

}