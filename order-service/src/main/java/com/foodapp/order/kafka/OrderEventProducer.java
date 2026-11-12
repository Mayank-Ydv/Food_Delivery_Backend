package com.foodapp.order.kafka;

import com.foodapp.order.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Publishes an event to Kafka's "order-events" topic every time an
 * order's status changes. The notification-service listens to this
 * topic and reacts (sends email/SMS) — completely decoupled, completely async.
 *
 * This is THE thing to mention in interviews: "I used Kafka to decouple
 * order processing from notification delivery, so a slow email provider
 * never blocks the order flow."
 */
@Service
public class OrderEventProducer {

    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, OrderEventPayload> kafkaTemplate;

    @Autowired
    public OrderEventProducer(KafkaTemplate<String, OrderEventPayload> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderStatusChanged(Order order) {
        OrderEventPayload payload = new OrderEventPayload(
                order.getId(),
                order.getUserId(),
                order.getStatus().name(),
                "ORDER_STATUS_CHANGED",
                System.currentTimeMillis()
        );

        // key = orderId as String -> Kafka guarantees ordering of events for the SAME order
        kafkaTemplate.send(TOPIC, order.getId().toString(), payload);
        System.out.println("Published Kafka event: " + payload);
    }
}
