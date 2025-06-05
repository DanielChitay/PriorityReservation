package com.example.priorityreservation.service;

import com.example.priorityreservation.dto.TaskResponseDTO;
import com.example.priorityreservation.dto.TaskRequestDTO;
import com.example.priorityreservation.dto.TaskStatusUpdateDTO;
import com.example.priorityreservation.exception.ResourceNotFoundException;
import com.example.priorityreservation.model.*;
import com.example.priorityreservation.model.Task.TaskStatus;
import com.example.priorityreservation.repository.ActionStackRepository;
import com.example.priorityreservation.repository.TaskHistoryRepository;
import com.example.priorityreservation.repository.TaskRepository;
import com.example.priorityreservation.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final ActionStackRepository actionStackRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final TaskAuditService taskAuditService; 
        private final TaskHistoryRepository taskHistoryRepository;
    public TaskResponseDTO createTask(TaskRequestDTO taskRequest) {
        Task task = taskRequest.toEntity();
        
        if (taskRequest.getAssignedUserId() != null) {
            User user = userRepository.findById(taskRequest.getAssignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + taskRequest.getAssignedUserId()));
            task.setAssignedUser(user);
        }
        
        if (taskRequest.getParentTaskId() != null) {
            Task parentTask = taskRepository.findById(taskRequest.getParentTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent task not found with id: " + taskRequest.getParentTaskId()));
            task.setParentTask(parentTask);
        }
        
        Task savedTask = taskRepository.save(task);
        saveTaskActionToStack(ActionStack.ActionType.CREATE, savedTask);
        sendTaskEvent("task.created", savedTask);
        
        return TaskResponseDTO.fromEntity(savedTask);
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return TaskResponseDTO.fromEntity(task);
    }

    public TaskResponseDTO updateTask(Long id, TaskRequestDTO taskRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        
        // Guardar estado anterior
        Map<String, Object> oldState = createTaskStateMap(task);
        
        // Actualizar campos básicos
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setPriority(taskRequest.getPriority());
        
        // Actualizar usuario asignado
        if (taskRequest.getAssignedUserId() != null) {
            User user = userRepository.findById(taskRequest.getAssignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + taskRequest.getAssignedUserId()));
            task.setAssignedUser(user);
        } else {
            task.setAssignedUser(null);
        }
        
        // Actualizar tarea padre
        if (taskRequest.getParentTaskId() != null) {
            Task parentTask = taskRepository.findById(taskRequest.getParentTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent task not found with id: " + taskRequest.getParentTaskId()));
            task.setParentTask(parentTask);
        } else {
            task.setParentTask(null);
        }
        
        Task updatedTask = taskRepository.save(task);
        
        // Guardar acción con estado anterior y nuevo
        Map<String, Object> actionData = new HashMap<>();
        actionData.put("old", oldState);
        actionData.put("new", createTaskStateMap(updatedTask));
        saveActionToStack(ActionStack.ActionType.UPDATE, ActionStack.EntityType.TASK, id, actionData);
        
        sendTaskEvent("task.updated", updatedTask);
        
        return TaskResponseDTO.fromEntity(updatedTask);
    }

@Transactional(readOnly = true)
public List<TaskResponseDTO> getTasksByStatus(Task.TaskStatus status) {
    return taskRepository.findByStatus(status).stream()
            .map(TaskResponseDTO::fromEntity)
            .collect(Collectors.toList()); // Usar collect en lugar de toList() para mayor compatibilidad
}

public TaskResponseDTO updateTaskStatus(Long id, TaskStatusUpdateDTO statusUpdate) {
    Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    
    validateStatusTransition(task.getStatus(), statusUpdate.getStatus());
    
    // Guardar estado anterior
    String oldStatus = task.getStatus().name();
    
    // Actualizar tarea
    task.setStatus(statusUpdate.getStatus());
    Task updatedTask = taskRepository.save(task);
    
    // >>>>>>>>> SOLO ESTO ES NUEVO - Registro en tu tabla existente <<<<<<<<<<
    TaskHistory history = new TaskHistory();
    history.setTask(task); // Relación ManyToOne que ya tienes
    history.setFieldName("status");
    history.setOldValue(oldStatus);
    history.setNewValue(statusUpdate.getStatus().name());
    history.setChangedAt(LocalDateTime.now());
    // changedBy lo puedes setear si necesitas
    taskHistoryRepository.save(history);
    // >>>>>>>>> FIN DEL CAMBIO <<<<<<<<<<
    
    // Tu código original sigue igual
    Map<String, Object> statusData = Map.of(
        "oldStatus", oldStatus,
        "newStatus", statusUpdate.getStatus().name()
    );
    saveActionToStack(ActionStack.ActionType.STATUS_CHANGE, ActionStack.EntityType.TASK, id, statusData);
    
    String eventType = statusUpdate.getStatus() == Task.TaskStatus.COMPLETED ? 
                      "task.completed" : "task.status_changed";
    sendTaskEvent(eventType, updatedTask);
    
    return TaskResponseDTO.fromEntity(updatedTask);
}
    
    private TaskResponseDTO undoCreateAction(ActionStack action) throws JsonProcessingException {
    taskRepository.deleteById(action.getEntityId());
    return null;
}

private TaskResponseDTO undoUpdateAction(ActionStack action) throws JsonProcessingException {
    Map<?, ?> oldState = objectMapper.readValue(action.getEntityData(), Map.class);
    
    Task task = taskRepository.findById(action.getEntityId())
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    
    // Restaurar propiedades básicas
    task.setTitle((String) oldState.get("title"));
    task.setDescription((String) oldState.get("description"));
    task.setPriority(Task.TaskPriority.valueOf((String) oldState.get("priority")));
    task.setStatus(Task.TaskStatus.valueOf((String) oldState.get("status")));
    
    // Restaurar relaciones
    if (oldState.get("assignedUserId") != null) {
        User user = userRepository.findById(Long.valueOf(oldState.get("assignedUserId").toString()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        task.setAssignedUser(user);
    } else {
        task.setAssignedUser(null);
    }
    
    if (oldState.get("parentTaskId") != null) {
        Task parent = taskRepository.findById(Long.valueOf(oldState.get("parentTaskId").toString()))
                .orElseThrow(() -> new ResourceNotFoundException("Parent task not found"));
        task.setParentTask(parent);
    } else {
        task.setParentTask(null);
    }
    
    Task restoredTask = taskRepository.save(task);
    return TaskResponseDTO.fromEntity(restoredTask);
}

private TaskResponseDTO undoDeleteAction(ActionStack action) throws JsonProcessingException {
    Map<String, Object> taskData = objectMapper.readValue(action.getEntityData(), Map.class);
    
    Task task = new Task();
    task.setTitle((String) taskData.get("title"));
    task.setDescription((String) taskData.get("description"));
    task.setStatus(Task.TaskStatus.valueOf((String) taskData.get("status")));
    task.setPriority(Task.TaskPriority.valueOf((String) taskData.get("priority")));
    
    // Restaurar relaciones
    if (taskData.get("assignedUserId") != null) {
        User user = userRepository.findById(Long.valueOf(taskData.get("assignedUserId").toString()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        task.setAssignedUser(user);
    }
    
    if (taskData.get("parentTaskId") != null) {
        Task parent = taskRepository.findById(Long.valueOf(taskData.get("parentTaskId").toString()))
                .orElseThrow(() -> new ResourceNotFoundException("Parent task not found"));
        task.setParentTask(parent);
    }
    
    Task recreatedTask = taskRepository.save(task);
    return TaskResponseDTO.fromEntity(recreatedTask);
}

private TaskResponseDTO undoStatusChangeAction(ActionStack action) throws JsonProcessingException {
    Map<?, ?> statusData = objectMapper.readValue(action.getEntityData(), Map.class);
    String oldStatus = (String) statusData.get("oldStatus");
    
    Task task = taskRepository.findById(action.getEntityId())
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    
    task.setStatus(Task.TaskStatus.valueOf(oldStatus));
    Task updatedTask = taskRepository.save(task);
    
    return TaskResponseDTO.fromEntity(updatedTask);
}
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        
        // Guardar datos completos de la tarea eliminada
        saveTaskActionToStack(ActionStack.ActionType.DELETE, task);
        taskRepository.delete(task);
        
        sendTaskEvent("task.deleted", task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return taskRepository.findByAssignedUserId(userId).stream()
                .map(TaskResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getSubtasks(Long parentTaskId) {
        if (!taskRepository.existsById(parentTaskId)) {
            throw new ResourceNotFoundException("Parent task not found with id: " + parentTaskId);
        }
        return taskRepository.findByParentTaskId(parentTaskId).stream()
                .map(TaskResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getRootTasks() {
        return taskRepository.findByParentTaskIsNull().stream()
                .map(TaskResponseDTO::fromEntity)
                .toList();
    }


    // Métodos auxiliares
    private Map<String, Object> createTaskStateMap(Task task) {
        Map<String, Object> state = new HashMap<>();
        state.put("title", task.getTitle());
        state.put("description", task.getDescription());
        state.put("status", task.getStatus().name());
        state.put("priority", task.getPriority().name());
        state.put("assignedUserId", task.getAssignedUser() != null ? task.getAssignedUser().getId() : null);
        state.put("parentTaskId", task.getParentTask() != null ? task.getParentTask().getId() : null);
        return state;
    }

    private void saveTaskActionToStack(ActionStack.ActionType actionType, Task task) {
        saveActionToStack(actionType, ActionStack.EntityType.TASK, task.getId(), createTaskStateMap(task));
    }

private void saveActionToStack(ActionStack.ActionType actionType, 
                             ActionStack.EntityType entityType, 
                             Long entityId, 
                             Object entityData) {
    try {
        ActionStack action = ActionStack.builder()
            .actionType(actionType)
            .entityType(entityType)
            .entityId(entityId)
            .entityData(objectMapper.writeValueAsString(entityData))
            .build();
            // performedAt se establece automáticamente por @CreationTimestamp
        actionStackRepository.save(action);
    } catch (JsonProcessingException e) {
        throw new RuntimeException("Error saving action to stack", e);
    }
}

@Transactional
public Task updateTaskStatus(Long taskId, TaskStatus newStatus) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new EntityNotFoundException("Task not found"));
    
    TaskStatus oldStatus = task.getStatus();
    task.changeStatus(newStatus);
    
    // Guardar la tarea y el historial en la misma transacción
    Task updatedTask = taskRepository.save(task);
    taskAuditService.recordStatusChange(task, oldStatus, newStatus);
    
    eventPublisher.publishTaskUpdatedEvent(updatedTask);
        
    if (newStatus == TaskStatus.COMPLETED) {
            eventPublisher.publishTaskCompletedEvent(updatedTask);
     }
    
    return updatedTask;
}
public TaskResponseDTO undoLastAction() throws JsonProcessingException {
    ActionStack lastAction = actionStackRepository.findTopByOrderByPerformedAtDesc()
            .orElseThrow(() -> new IllegalStateException("No actions to undo"));
    
    try {
        switch (lastAction.getActionType()) {
            case CREATE:
                return undoCreateAction(lastAction);
            case UPDATE:
                return undoUpdateAction(lastAction);
            case DELETE:
                return undoDeleteAction(lastAction);
            case STATUS_CHANGE:
                return undoStatusChangeAction(lastAction);
            default:
                throw new IllegalStateException("Unknown action type");
        }
    } finally {
        actionStackRepository.delete(lastAction);
    }
}

    private void validateStatusTransition(Task.TaskStatus currentStatus, Task.TaskStatus newStatus) {
        if (currentStatus == Task.TaskStatus.COMPLETED) {
            throw new IllegalStateException("Cannot change status from COMPLETED");
        }
        
        if (newStatus == Task.TaskStatus.PENDING && currentStatus == Task.TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot revert from IN_PROGRESS to PENDING");
        }
    }

    private void sendTaskEvent(String routingKey, Task task) {
        try {
            rabbitTemplate.convertAndSend("task.events", routingKey, 
                objectMapper.writeValueAsString(TaskResponseDTO.fromEntity(task)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error sending task event", e);
        }
    }

 
    // Métodos de búsqueda
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> searchByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(TaskResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> searchByDescription(String description) {
        return taskRepository.findByDescriptionContainingIgnoreCase(description).stream()
                .map(TaskResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean existsByTitleAndParent(String title, Long parentTaskId) {
        return taskRepository.existsByTitleAndParentTaskId(title, parentTaskId);
    }
    @Transactional
    public Task createTask(Task task) {
        Task savedTask = taskRepository.save(task);
        eventPublisher.publishTaskCreatedEvent(savedTask);
        return savedTask;
    }


}