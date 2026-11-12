package com.foodapp.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Runs on port 8082.
 * Manages restaurants and menus. Uses Redis to cache menu reads since
 * menus are read FAR more often than they're updated (classic caching case).
 */
@SpringBootApplication
@EnableCaching
public class RestaurantServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
        System.out.println("Restaurant Service started on http://localhost:8082");
    }
}
