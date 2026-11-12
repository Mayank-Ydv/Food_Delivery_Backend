package com.foodapp.restaurant.service;

import com.foodapp.restaurant.entity.MenuItem;
import com.foodapp.restaurant.entity.Restaurant;
import com.foodapp.restaurant.exception.ResourceNotFoundException;
import com.foodapp.restaurant.repository.MenuItemRepository;
import com.foodapp.restaurant.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> searchByName(String name) {
        return restaurantRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * THIS is the caching showcase method.
     *
     * @Cacheable("menus") -> on first call, runs the DB query and stores
     * the result in Redis under key "menus::<restaurantId>". Every call
     * after that (within the 10-min TTL set in RedisCacheConfig) returns
     * straight from Redis — the database is never touched again until
     * the cache expires or is evicted.
     * "menus are read 100x more than they're written, so I cache them."
     */
    @Cacheable(value = "menus", key = "#restaurantId")
    public List<MenuItem> getMenuForRestaurant(Long restaurantId) {
        System.out.println("CACHE MISS — hitting database for restaurant " + restaurantId);
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    /**
     * Whenever a menu item is added/updated, we must EVICT the cache for
     * that restaurant — otherwise users would see stale menu data for up
     * to 10 minutes after an update. This pairing (@Cacheable + @CacheEvict)
     * is the standard cache-invalidation pattern.
     */
    @CacheEvict(value = "menus", key = "#restaurantId")
    public MenuItem addMenuItem(Long restaurantId, MenuItem menuItem) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        menuItem.setRestaurant(restaurant);
        return menuItemRepository.save(menuItem);
    }

    /**
     * Simple geolocation search using the Haversine formula — calculates
     * great-circle distance between the user's coordinates and each
     * restaurant, then filters/sorts by distance. No external API needed.
     */
    public List<Restaurant> findNearby(double userLat, double userLng, double radiusKm) {
        return restaurantRepository.findAll().stream()
                .filter(r -> r.getLatitude() != null && r.getLongitude() != null)
                .filter(r -> haversineDistance(userLat, userLng, r.getLatitude(), r.getLongitude()) <= radiusKm)
                .sorted((a, b) -> Double.compare(
                        haversineDistance(userLat, userLng, a.getLatitude(), a.getLongitude()),
                        haversineDistance(userLat, userLng, b.getLatitude(), b.getLongitude())
                ))
                .toList();
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
