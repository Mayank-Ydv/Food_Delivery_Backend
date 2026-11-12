package com.foodapp.order.service;

import com.foodapp.order.dto.OrderDtos.*;
import com.foodapp.order.entity.Order;
import com.foodapp.order.entity.OrderItem;
import com.foodapp.order.exception.ResourceNotFoundException;
import com.foodapp.order.kafka.OrderEventProducer;
import com.foodapp.order.repository.OrderRepository;
import com.foodapp.order.statemachine.OrderEvent;
import com.foodapp.order.statemachine.OrderState;
import com.foodapp.order.statemachine.OrderStateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStateMachine stateMachine;
    private final OrderEventProducer eventProducer;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderStateMachine stateMachine, OrderEventProducer eventProducer) {
        this.orderRepository = orderRepository;
        this.stateMachine = stateMachine;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public Order placeOrder(PlaceOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setRestaurantId(request.getRestaurantId());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setStatus(OrderState.PLACED);

        List<OrderItem> items = request.getItems().stream().map(itemReq -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setMenuItemId(itemReq.getMenuItemId());
            item.setItemName(itemReq.getItemName());
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(itemReq.getPrice());
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);

        double total = items.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        // Publish "order placed" event -> notification-service sends confirmation
        eventProducer.publishOrderStatusChanged(saved);

        return saved;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * THE KEY METHOD. Applies a state machine transition, persists it,
     * and publishes a Kafka event so other services react asynchronously.
     *
     * Example call: transitionOrder(5L, "CONFIRM_ORDER")
     * -> validates PLACED + CONFIRM_ORDER is allowed -> moves to CONFIRMED
     * -> saves -> publishes Kafka event -> notification-service sends SMS
     */
    @Transactional
    public Order transitionOrder(Long orderId, String eventName) {
        Order order = getOrderById(orderId);
        OrderEvent event = OrderEvent.valueOf(eventName.toUpperCase());

        OrderState newState = stateMachine.transition(order.getStatus(), event);
        order.setStatus(newState);

        Order saved = orderRepository.save(order);
        eventProducer.publishOrderStatusChanged(saved);

        return saved;
    }
}
