package com.foodapp.order.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * MUST match the shape of OrderEventPayload in order-service exactly
 * (field names matter for JSON deserialization). In a real production
 * system, this shared shape would live in a common library / schema
 * registry (Avro/Protobuf) instead of being copy-pasted across services.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventPayload implements Serializable {
    private Long orderId;
    private Long userId;
    private String status;
    private String eventType;
    private long timestamp;
}
