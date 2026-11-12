package com.foodapp.delivery.controller;

import com.foodapp.delivery.entity.DeliveryAgent;
import com.foodapp.delivery.entity.DeliveryAssignment;
import com.foodapp.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *   GET  http://localhost:8084/api/delivery/agents/available
 *   POST http://localhost:8084/api/delivery/agents
 *   POST http://localhost:8084/api/delivery/assign/{orderId}
 *   PATCH http://localhost:8084/api/delivery/deliver/{orderId}
 *   GET  http://localhost:8084/api/delivery/order/{orderId}
 */
@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/agents/available")
    public ResponseEntity<List<DeliveryAgent>> availableAgents() {
        return ResponseEntity.ok(deliveryService.getAvailableAgents());
    }

    @PostMapping("/agents")
    public ResponseEntity<DeliveryAgent> registerAgent(@Valid @RequestBody DeliveryAgent agent) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.registerAgent(agent));
    }

    @PostMapping("/assign/{orderId}")
    public ResponseEntity<DeliveryAssignment> assign(@PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.assignAgentToOrder(orderId));
    }

    @PatchMapping("/deliver/{orderId}")
    public ResponseEntity<DeliveryAssignment> markDelivered(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.markDelivered(orderId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryAssignment> getAssignment(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getAssignmentForOrder(orderId));
    }
}
