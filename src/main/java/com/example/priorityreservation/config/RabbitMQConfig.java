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
    
    @Bean
    public Queue tasksQueue() {
        return new Queue(QUEUE_TASKS, true); // true para durable
    }
    
    @Bean
    public TopicExchange tasksExchange() {
        return new TopicExchange(EXCHANGE_TASKS);
    }
    
    @Bean
    public Binding tasksBinding(Queue tasksQueue, TopicExchange tasksExchange) {
        return BindingBuilder.bind(tasksQueue)
               .to(tasksExchange)
               .with(ROUTING_KEY);
    }
}