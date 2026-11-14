package com.foodapp.order.kafka;

import com.foodapp.notification.service.EmailService;
import com.foodapp.notification.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * THE CONSUMER SIDE of the async architecture.
 *
 * @KafkaListener subscribes this method to the "order-events" topic.
 * Spring Kafka calls onOrderEvent(...) automatically every time order-service
 * publishes a message — no polling code needed, it's all handled for you.
 *
 * groupId = "notification-group" matches what's set in KafkaConsumerConfig.
 * If you ran 3 instances of this service, Kafka would automatically load-balance
 * messages across them (this is the "scalability" story for interviews).
 */
@Service
public class OrderEventConsumer {

    private final EmailService emailService;
    private final SmsService smsService;

    @Autowired
    public OrderEventConsumer(EmailService emailService, SmsService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }

    // @KafkaListener(topics = "order-events", groupId = "notification-group")
    // public void onOrderEvent(OrderEventPayload event) {
    //     System.out.println("Received Kafka event: " + event);

    //     String message = buildMessageForStatus(event.getStatus());

    //     emailService.sendEmail(event.getUserId(), message);
    //     smsService.sendSms(event.getUserId(), message);
    // }
@KafkaListener(topics = "order-events", groupId = "notification-group")
    public void onOrderEvent(OrderEventPayload event) {
        System.out.println("Received Kafka event: " + event);

        String message = buildMessageForStatus(event.getStatus());

        // userId ki jagah actual recipient email string pass karein
        String recipientEmail = "your-actual-email@gmail.com"; 

        emailService.sendEmail(recipientEmail, message);
        smsService.sendSms(event.getUserId(), message);
    }
    private String buildMessageForStatus(String status) {
        return switch (status) {
            case "PLACED" -> "Your order has been placed successfully!";
            case "CONFIRMED" -> "Your order has been confirmed by the restaurant.";
            case "PREPARING" -> "Your food is being prepared.";
            case "OUT_FOR_DELIVERY" -> "Your order is out for delivery!";
            case "DELIVERED" -> "Your order has been delivered. Enjoy your meal!";
            case "CANCELLED" -> "Your order has been cancelled.";
            default -> "Your order status has been updated to " + status;
        };
    }
}
