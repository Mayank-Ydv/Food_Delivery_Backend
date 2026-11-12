package com.foodapp.order.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @Column(nullable = false)
    private Long menuItemId; // references MenuItem in restaurant-service

    @Column(nullable = false)
    private String itemName; // denormalized snapshot — survives even if menu item is later edited/deleted

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price; // price snapshot at time of order
}
