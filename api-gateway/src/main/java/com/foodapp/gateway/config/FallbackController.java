package com.foodapp.gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Called automatically by Resilience4j whenever a downstream service's
 * circuit breaker trips (e.g. order-service is down or too slow).
 * Returns a clean response instead of letting the client hang or see
 * a raw connection-refused error.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> orderServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Order service is temporarily unavailable. Please try again shortly."));
    }

    @GetMapping("/restaurants")
    public ResponseEntity<Map<String, Object>> restaurantServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Restaurant service is temporarily unavailable. Please try again shortly."));
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "User service is temporarily unavailable. Please try again shortly."));
    }
}
