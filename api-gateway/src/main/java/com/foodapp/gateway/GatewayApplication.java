package com.foodapp.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Runs on port 8080 — THE single entry point for every client request.
 * Routes traffic to the correct downstream service, validates JWTs,
 * and wraps calls with circuit breakers so one failing service doesn't
 * cascade and take down the whole system.
 */
@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("API Gateway started on http://localhost:8080");
    }
}
