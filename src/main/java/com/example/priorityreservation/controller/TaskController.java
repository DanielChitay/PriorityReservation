package com.example.priorityreservation.controller;

import com.example.priorityreservation.dto.TaskResponseDTO;
import com.example.priorityreservation.model.Task;
import com.example.priorityreservation.dto.TaskRequestDTO;
import com.example.priorityreservation.dto.TaskStatusUpdateDTO;
import com.example.priorityreservation.model.Priority;
import com.example.priorityreservation.model.Status;
import com.example.priorityreservation.service.TaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "APIs for managing tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new task")
    public TaskResponseDTO createTask(@Valid @RequestBody TaskRequestDTO taskRequest) {
        return taskService.createTask(taskRequest);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID")
    public TaskResponseDTO getTask(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task completely")
    public TaskResponseDTO updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDTO taskRequest) {
        return taskService.updateTask(id, taskRequest);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status")
    public TaskResponseDTO updateTaskStatus(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateDTO statusUpdate) {
        return taskService.updateTaskStatus(id, statusUpdate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a task")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all tasks for a user")
    public List<TaskResponseDTO> getTasksByUser(@PathVariable Long userId) {
        return taskService.getTasksByUser(userId);
    }

    @GetMapping("/{parentTaskId}/subtasks")
    @Operation(summary = "Get all subtasks for a parent task")
    public List<TaskResponseDTO> getSubtasks(@PathVariable Long parentTaskId) {
        return taskService.getSubtasks(parentTaskId);
    }

    @GetMapping("/root")
    @Operation(summary = "Get all root tasks (no parent)")
    public List<TaskResponseDTO> getRootTasks() {
        return taskService.getRootTasks();
    }

    @PostMapping("/undo")
    @Operation(summary = "Undo the last action")
    public ResponseEntity<TaskResponseDTO> undoLastAction() throws JsonProcessingException {
        TaskResponseDTO result = taskService.undoLastAction();
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.noContent().build();
    }
}