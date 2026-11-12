package com.foodapp.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore
    @JsonBackReference
    private Restaurant restaurant;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String description;

    @Positive
    @Column(nullable = false)
    private Double price;

    private Boolean available = true;

    private String category; // e.g. "Starters", "Main Course", "Desserts"
}
