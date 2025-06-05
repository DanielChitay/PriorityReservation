package com.example.priorityreservation.service;

import com.example.priorityreservation.dto.TaskStatusUpdateDTO;
import com.example.priorityreservation.model.Task;
import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.Comparator;
import org.springframework.stereotype.Service;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
public class TaskScheduler {

    private final PriorityQueue<ScheduledTask> taskQueue = new PriorityQueue<>(
            Comparator.comparing(ScheduledTask::getExecutionTime)
    );
    
    private final TaskService taskService;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public TaskScheduler(TaskService taskService) {
        this.taskService = taskService;
        startScheduler();
    }

    public void scheduleTask(Task task, LocalDateTime executionTime) {
        ScheduledTask scheduledTask = new ScheduledTask(task, executionTime);
        taskQueue.add(scheduledTask);
    }

    private void startScheduler() {
        executorService.scheduleAtFixedRate(this::checkScheduledTasks, 0, 1, TimeUnit.MINUTES);
    }

    private void checkScheduledTasks() {
        LocalDateTime now = LocalDateTime.now();
        
        while (!taskQueue.isEmpty() && taskQueue.peek().getExecutionTime().isBefore(now)) {
            ScheduledTask scheduledTask = taskQueue.poll();
            Task task = scheduledTask.getTask();
            
            taskService.updateTaskStatus(task.getId(), 
                    new TaskStatusUpdateDTO(Task.TaskStatus.IN_PROGRESS));
        }
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }

    @Getter
    @AllArgsConstructor
    private static class ScheduledTask {
        private final Task task;
        private final LocalDateTime executionTime;
    }
}