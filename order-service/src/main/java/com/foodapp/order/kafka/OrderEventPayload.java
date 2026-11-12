package com.foodapp.order.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * This is the message that gets published to Kafka whenever an order's
 * state changes. The notification-service consumes this exact shape.
 *
 * Keep event payloads SMALL and focused — don't dump the whole Order
 * entity into Kafka. Send just enough for consumers to act on.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventPayload implements Serializable {
    private Long orderId;
    private Long userId;
    private String status;     // e.g. "CONFIRMED"
    private String eventType;  // e.g. "ORDER_STATUS_CHANGED"
    private long timestamp;
}
