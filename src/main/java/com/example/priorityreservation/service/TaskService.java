package com.example.priorityreservation.service;

import com.example.priorityreservation.dto.TaskRequestDTO;
import com.example.priorityreservation.exception.TaskNotFoundException;
import com.example.priorityreservation.exception.UserNotFoundException;
import com.example.priorityreservation.model.*;
import com.example.priorityreservation.repository.TaskRepository;
import com.example.priorityreservation.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final UndoManager undoManager;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                     UserRepository userRepository,
                     RabbitTemplate rabbitTemplate,
                     UndoManager undoManager) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.undoManager = undoManager;
    }

    @Transactional
    public void executeTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        
        task.setStatus(Status.IN_PROGRESS);
        Task updatedTask = taskRepository.save(task);
        rabbitTemplate.convertAndSend("tasks.exchange", "task.started", updatedTask);
    }

@Transactional
public Task createTask(TaskRequestDTO taskDTO) {
    // Validación de prioridad mejorada
    Priority priority;
    try {
        priority = Priority.valueOf(taskDTO.getPriority().toUpperCase());
    } catch (IllegalArgumentException | NullPointerException e) {
        throw new IllegalArgumentException("Valor de prioridad inválido. Use HIGH, MEDIUM o LOW");
    }

    User user = userRepository.findById(taskDTO.getUserId())
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + taskDTO.getUserId()));

    Task task = new Task();
    task.setTitle(taskDTO.getTitle());
    task.setDescription(taskDTO.getDescription());
    task.setPriority(priority); // Asegurar que Priority es el tipo correcto
    task.setStatus(Status.PENDING); // Asegurar que Status es el tipo correcto
    task.setDueDate(taskDTO.getDueDate());
    task.setAssignedUser(user);

    Task savedTask = taskRepository.save(task);

    undoManager.registerAction(() -> {
        taskRepository.deleteById(savedTask.getId());
        rabbitTemplate.convertAndSend("tasks.exchange", "task.deleted", savedTask.getId());
    });

    rabbitTemplate.convertAndSend("tasks.exchange", "task.created", savedTask);
    return savedTask;
}

    @Transactional
    public Task markTaskAsCompleted(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Tarea no encontrada con ID: " + taskId));

        if (!task.getStatus().equals(Status.IN_PROGRESS)) {
            throw new IllegalStateException("Solo tareas EN PROGRESO pueden marcarse como completadas");
        }

        task.setStatus(Status.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        Task updatedTask = taskRepository.save(task);

        rabbitTemplate.convertAndSend("tasks.exchange", "task.completed", updatedTask);

        return updatedTask;
    }

    public List<Task> getTasksByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Usuario no encontrado con ID: " + userId);
        }
        return taskRepository.findByAssignedUserId(userId);
    }

    public List<Task> getTasksByPriority(String priority) {
        try {
            Priority priorityEnum = Priority.valueOf(priority.toUpperCase());
            return taskRepository.findByPriority(priorityEnum);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor de prioridad inválido: " + priority);
        }
    }

    @Transactional
    public Task updateTaskStatus(Long taskId, Status newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Tarea no encontrada con ID: " + taskId));

        // Validar transición de estado
        if (task.getStatus().equals(Status.COMPLETED) && !newStatus.equals(Status.COMPLETED)) {
            throw new IllegalStateException("Tareas COMPLETADAS no pueden modificarse");
        }

        task.setStatus(newStatus);
        
        if (newStatus.equals(Status.COMPLETED)) {
            task.setCompletedAt(LocalDateTime.now());
        }
        
        Task updatedTask = taskRepository.save(task);
        
        // Notificar cambio de estado
        rabbitTemplate.convertAndSend("tasks.exchange", "task.updated", updatedTask);
        
        return updatedTask;
    }
}