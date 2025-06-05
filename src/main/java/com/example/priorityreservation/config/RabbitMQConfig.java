package com.example.priorityreservation.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    
    public static final String QUEUE_TASKS = "tasks.queue";
    public static final String EXCHANGE_TASKS = "tasks.exchange";
    public static final String ROUTING_KEY = "tasks.routingkey";
    
    public static final String EXCHANGE_TASK_EVENTS = "task.events";
    
    @Bean
    public Queue tasksQueue() {
        return new Queue(QUEUE_TASKS, true); 
    }
    
    @Bean
    public TopicExchange tasksExchange() {
        return new TopicExchange(EXCHANGE_TASKS, true, false); 
    }
    
    @Bean
    public Binding tasksBinding() {
        return BindingBuilder.bind(tasksQueue())
               .to(tasksExchange())
               .with(ROUTING_KEY);
    }

    @Bean
    public DirectExchange taskEventsExchange() {
        return new DirectExchange(EXCHANGE_TASK_EVENTS, true, false);
    }

    @Bean
    public Queue taskCreatedQueue() {
        return new Queue("task.created.queue", true); 
    }

    @Bean
    public Queue taskUpdatedQueue() {
        return new Queue("task.updated.queue", true);
    }

    @Bean
    public Queue taskCompletedQueue() {
        return new Queue("task.completed.queue", true);
    }


    @Bean
    public Binding bindingCreated() {
        return BindingBuilder.bind(taskCreatedQueue())
               .to(taskEventsExchange())
               .with("task.created");
    }

    @Bean
    public Binding bindingUpdated() {
        return BindingBuilder.bind(taskUpdatedQueue())
               .to(taskEventsExchange())
               .with("task.updated");
    }

    @Bean
    public Binding bindingCompleted() {
        return BindingBuilder.bind(taskCompletedQueue())
               .to(taskEventsExchange())
               .with("task.completed");
    }
}