package com.foodapp.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Runs on port 8083.
 * The heart of the system: order placement, state transitions, Kafka events.
 */
@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("Order Service started on http://localhost:8083");
    }
}
