package com.foodapp.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

public class OrderDtos {

    @Data
    public static class PlaceOrderRequest {
        @NotNull
        private Long userId;

        @NotNull
        private Long restaurantId;

        @NotNull
        private String deliveryAddress;

        @NotEmpty(message = "Order must have at least one item")
        @Valid
        private List<OrderItemRequest> items;
    }

    @Data
    public static class OrderItemRequest {
        @NotNull
        private Long menuItemId;

        @NotNull
        private String itemName;

        @Positive
        private Integer quantity;

        @Positive
        private Double price;
    }

    @Data
    public static class TransitionRequest {
        @NotNull(message = "event is required, e.g. CONFIRM_ORDER")
        private String event;
    }
}
