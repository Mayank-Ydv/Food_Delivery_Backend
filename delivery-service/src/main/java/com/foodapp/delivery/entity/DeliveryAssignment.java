package com.foodapp.delivery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId; // references Order in order-service

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private DeliveryAgent agent;

    private LocalDateTime assignedAt;
    private LocalDateTime deliveredAt;

    private Integer estimatedMinutes; // simple ETA estimate

    @PrePersist
    protected void onCreate() {
        this.assignedAt = LocalDateTime.now();
    }
}
