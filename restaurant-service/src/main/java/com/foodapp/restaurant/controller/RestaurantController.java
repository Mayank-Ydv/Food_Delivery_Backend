package com.foodapp.restaurant.controller;

import com.foodapp.restaurant.entity.MenuItem;
import com.foodapp.restaurant.entity.Restaurant;
import com.foodapp.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *   GET  http://localhost:8082/api/restaurants
 *   GET  http://localhost:8082/api/restaurants/1
 *   POST http://localhost:8082/api/restaurants
 *   GET  http://localhost:8082/api/restaurants/search?name=pizza
 *   GET  http://localhost:8082/api/restaurants/nearby?lat=28.6&lng=77.2&radius=5
 *   GET  http://localhost:8082/api/restaurants/1/menu              <- cached in Redis
 *   POST http://localhost:8082/api/restaurants/1/menu
 */
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAll() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @PostMapping
    public ResponseEntity<Restaurant> create(@Valid @RequestBody Restaurant restaurant) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.createRestaurant(restaurant));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Restaurant>> search(@RequestParam String name) {
        return ResponseEntity.ok(restaurantService.searchByName(name));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Restaurant>> nearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radius) {
        return ResponseEntity.ok(restaurantService.findNearby(lat, lng, radius));
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<List<MenuItem>> getMenu(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getMenuForRestaurant(id));
    }

    @PostMapping("/{id}/menu")
    public ResponseEntity<MenuItem> addMenuItem(@PathVariable Long id, @Valid @RequestBody MenuItem menuItem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.addMenuItem(id, menuItem));
    }
}
