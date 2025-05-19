package com.example.priorityreservation.service;

import com.example.priorityreservation.model.Task;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Queue;
import java.util.LinkedList;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TaskScheduler {
    private final Queue<Task> scheduledTasks = new LinkedList<>();
    private final TaskService taskService;

    @Autowired
    public TaskScheduler(TaskService taskService) {
        this.taskService = taskService;
    }

    public void scheduleTask(Task task) {
        scheduledTasks.add(task);
    }

    @Scheduled(fixedRate = 5000) // Ejecuta cada 5 segundos
    public void processScheduledTasks() {
        while (!scheduledTasks.isEmpty()) {
            Task task = scheduledTasks.poll();
            taskService.executeTask(task);
        }
    }
}