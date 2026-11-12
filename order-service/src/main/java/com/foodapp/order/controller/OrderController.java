package com.foodapp.order.controller;

import com.foodapp.order.dto.OrderDtos.*;
import com.foodapp.order.entity.Order;
import com.foodapp.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *   POST  http://localhost:8083/api/orders                    -> place order
 *   GET   http://localhost:8083/api/orders/1                  -> get order by id
 *   GET   http://localhost:8083/api/orders/user/3              -> get all orders for a user
 *   PATCH http://localhost:8083/api/orders/1/transition         -> move order to next state
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @PatchMapping("/{id}/transition")
    public ResponseEntity<Order> transitionOrder(@PathVariable Long id, @Valid @RequestBody TransitionRequest request) {
        return ResponseEntity.ok(orderService.transitionOrder(id, request.getEvent()));
    }
}
