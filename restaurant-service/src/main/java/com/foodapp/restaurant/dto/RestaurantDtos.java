package com.foodapp.restaurant.dto;

import lombok.Data;

public class RestaurantDtos {

    @Data
    public static class NearbySearchRequest {
        private double latitude;
        private double longitude;
        private double radiusKm = 5.0; // default 5km radius
    }
}
