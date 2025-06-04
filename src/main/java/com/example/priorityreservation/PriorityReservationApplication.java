package com.example.priorityreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.priorityreservation.repository")
@EntityScan(basePackages = "com.example.priorityreservation.model")
public class PriorityReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PriorityReservationApplication.class, args);
    }
}