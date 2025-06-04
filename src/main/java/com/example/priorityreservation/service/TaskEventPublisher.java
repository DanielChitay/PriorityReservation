/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.service;

/**
 *
 * @author rodol
 */
import com.example.priorityreservation.model.Task;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public TaskEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishTaskCreatedEvent(Task task) {
        rabbitTemplate.convertAndSend(
            "task.events", 
            "task.created", 
            task
        );
    }

    public void publishTaskUpdatedEvent(Task task) {
        rabbitTemplate.convertAndSend(
            "task.events", 
            "task.updated", 
            task
        );
    }

    public void publishTaskCompletedEvent(Task task) {
        rabbitTemplate.convertAndSend(
            "task.events", 
            "task.completed", 
            task
        );
    }
}