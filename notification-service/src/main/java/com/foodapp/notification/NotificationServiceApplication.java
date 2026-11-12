package com.foodapp.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Runs on port 8085.
 * Has NO REST endpoints of its own — it's purely a Kafka consumer that
 * reacts to events. This is the "decoupled" part of the architecture.
 */

@EnableKafka
@SpringBootApplication

@ComponentScan(basePackages = {"com.foodapp.notification", "com.foodapp.order.kafka"})
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
        System.out.println("Notification Service started — listening to Kafka on port 8085");
    }
}
