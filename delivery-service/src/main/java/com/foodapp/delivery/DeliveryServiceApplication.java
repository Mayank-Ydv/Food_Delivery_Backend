package com.foodapp.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Runs on port 8084.
 * Tracks delivery agents and their assignment to orders.
 */
@SpringBootApplication
public class DeliveryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
        System.out.println("Delivery Service started on http://localhost:8084");
    }
}
