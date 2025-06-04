package com.example.priorityreservation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskStatus status = TaskStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User assignedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Task> subtasks = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TaskHistory> history = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

   
    // getters y setters
    public TaskStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    public enum TaskStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }

    public enum TaskPriority {
        HIGH, MEDIUM, LOW
    }

    public void addSubtask(Task subtask) {
        subtasks.add(subtask);
        subtask.setParentTask(this);
    }

    public void removeSubtask(Task subtask) {
        subtasks.remove(subtask);
        subtask.setParentTask(null);
    }

public void changeStatus(TaskStatus newStatus) {
    if (this.status == TaskStatus.COMPLETED) {
        throw new IllegalStateException("Cannot change status from COMPLETED");
    }
    
    // Solo cambia el estado, la auditoría se maneja externamente
    this.status = newStatus;
}
}