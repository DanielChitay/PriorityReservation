package com.example.priorityreservation.service;

import com.example.priorityreservation.dto.TaskRequestDTO;
import com.example.priorityreservation.exception.TaskNotFoundException;
import com.example.priorityreservation.exception.UserNotFoundException;
import com.example.priorityreservation.model.*;
import com.example.priorityreservation.repository.TaskRepository;
import com.example.priorityreservation.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        
        task.setStatus(Status.IN_PROGRESS.toString());
        Task updatedTask = taskRepository.save(task);
        rabbitTemplate.convertAndSend("tasks.exchange", "task.started", updatedTask);
    }

public Task createTask(TaskRequestDTO taskDTO) {
    User user = userRepository.findById(taskDTO.getAssignedUserId())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
    Task task = new Task();
    task.setTitle(taskDTO.getTitle());
    task.setDescription(taskDTO.getDescription());
    task.setStatus(taskDTO.getStatus());
    task.setPriority(Priority.valueOf(taskDTO.getPriority()));
    task.setAssignedUser(user);
    
    return taskRepository.save(task);
}

    @Transactional
    public Task markTaskAsCompleted(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Tarea no encontrada con ID: " + taskId));

        if (!task.getStatus().equals(Status.IN_PROGRESS)) {
            throw new IllegalStateException("Solo tareas EN PROGRESO pueden marcarse como completadas");
        }

        task.setStatus(Status.COMPLETED.toString());
        task.setUpdatedAt(LocalDateTime.now());
        Task updatedTask = taskRepository.save(task);

        rabbitTemplate.convertAndSend("tasks.exchange", "task.completed", updatedTask);

        return updatedTask;
    }

    public Task updateTaskStatus(Long taskId, String newStatus) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new EntityNotFoundException("Task not found"));
    
    task.setStatus(newStatus);
    return taskRepository.save(task);
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

        task.setStatus(newStatus.toString());
        
        if (newStatus.equals(Status.COMPLETED)) {
            task.setUpdatedAt(LocalDateTime.now());
        }
        
        Task updatedTask = taskRepository.save(task);
        
        // Notificar cambio de estado
        rabbitTemplate.convertAndSend("tasks.exchange", "task.updated", updatedTask);
        
        return updatedTask;
    }
   public Optional<Task> getTaskById(Long taskId) {
    return taskRepository.findById(taskId);
}
    @Transactional(readOnly = true)
public List<Task> searchTasks(String title, Priority priority, Status status) {
    // Caso 1: Búsqueda por los 3 criterios
    if (title != null && priority != null && status != null) {
        return taskRepository.findByTitleContainingAndPriorityAndStatus(title, priority, status);
    }
    // Caso 2: Búsqueda por título y prioridad
    else if (title != null && priority != null) {
        return taskRepository.findByTitleContainingAndPriority(title, priority);
    }
    // Caso 3: Búsqueda por título y estado
    else if (title != null && status != null) {
        return taskRepository.findByTitleContainingAndStatus(title, status);
    }
    // Caso 4: Búsqueda por prioridad y estado
    else if (priority != null && status != null) {
        return taskRepository.findByPriorityAndStatus(priority, status);
    }
    // Caso 5: Búsqueda solo por título
    else if (title != null) {
        return taskRepository.findByTitleContaining(title);
    }
    // Caso 6: Búsqueda solo por prioridad
    else if (priority != null) {
        return taskRepository.findByPriority(priority);
    }
    // Caso 7: Búsqueda solo por estado
    else if (status != null) {
        return taskRepository.findByStatus(status);
    }
    // Caso 8: Sin filtros (devolver todas)
    else {
        return taskRepository.findAll();
    }
}
}