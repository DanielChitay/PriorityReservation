package com.example.priorityreservation.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    
    // Nombres para el sistema general de tareas (Topic Exchange)
    public static final String QUEUE_TASKS = "tasks.queue";
    public static final String EXCHANGE_TASKS = "tasks.exchange";
    public static final String ROUTING_KEY = "tasks.routingkey";
    
    // Nombres para eventos específicos (Direct Exchange)
    public static final String EXCHANGE_TASK_EVENTS = "task.events";
    
    // Configuración para el Topic Exchange (sistema general)
    @Bean
    public Queue tasksQueue() {
        return new Queue(QUEUE_TASKS, true); // true para durable
    }
    
    @Bean
    public TopicExchange tasksExchange() {
        return new TopicExchange(EXCHANGE_TASKS, true, false); // durable, no auto-delete
    }
    
    @Bean
    public Binding tasksBinding() {
        return BindingBuilder.bind(tasksQueue())
               .to(tasksExchange())
               .with(ROUTING_KEY);
    }
    
    // Configuración para el Direct Exchange (eventos específicos)
    @Bean
    public DirectExchange taskEventsExchange() {
        return new DirectExchange(EXCHANGE_TASK_EVENTS, true, false); // durable, no auto-delete
    }

    // Colas para eventos específicos
    @Bean
    public Queue taskCreatedQueue() {
        return new Queue("task.created.queue", true); // durable
    }

    @Bean
    public Queue taskUpdatedQueue() {
        return new Queue("task.updated.queue", true);
    }

    @Bean
    public Queue taskCompletedQueue() {
        return new Queue("task.completed.queue", true);
    }

    // Bindings para eventos específicos
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