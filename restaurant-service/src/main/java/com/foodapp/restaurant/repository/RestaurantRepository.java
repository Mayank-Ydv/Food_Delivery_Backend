package com.foodapp.restaurant.repository;

import com.foodapp.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByCuisineIgnoreCase(String cuisine);
    List<Restaurant> findByNameContainingIgnoreCase(String name);
}
