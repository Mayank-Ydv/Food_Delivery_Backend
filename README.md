# Real-Time Food Delivery Backend — Spring Boot Microservices

A 5-service food delivery backend with API Gateway, JWT auth, Kafka event-driven
notifications, Redis caching, and an order state machine. Built to mirror the
backend architecture used at companies like Swiggy, Zepto, and Razorpay.

## Services

| Service              | Port | Responsibility                                  |
|----------------------|------|--------------------------------------------------|
| api-gateway           | 8080 | Single entry point, JWT auth, circuit breaker     |
| user-service           | 8081 | Register, login, JWT issuing                      |
| restaurant-service      | 8082 | Restaurants, menus, Redis-cached reads, geo search |
| order-service           | 8083 | Order placement, state machine, Kafka producer     |
| delivery-service         | 8084 | Agent assignment and delivery tracking             |
| notification-service     | 8085 | Kafka consumer, sends email/SMS (mocked)           |

## Architecture

```
Client → API Gateway (8080) → [user / restaurant / order / delivery]-service
                                          order-service → Kafka → notification-service
```

## Prerequisites

- JDK 17+
- Maven (or use the included `./mvnw` if added per service)
- Docker Desktop

## Step 1 — start infrastructure (Postgres, Redis, Kafka)

From the root `food-delivery-backend/` folder:

```
docker compose up -d
```

This starts: Postgres (5432), Redis (6379), Zookeeper (2181), Kafka (9092).

Check everything is up:
```
docker ps
```
You should see 4 containers: `fd-postgres`, `fd-redis`, `fd-zookeeper`, `fd-kafka`.

## Step 2 — run each service (separate terminals, or separate VS Code windows)

Open each service folder in VS Code (or just open the root folder — the Java
extension detects all 6 Maven projects automatically) and run each
`*Application.java` file with F5, **in this order**:

1. `user-service` → `UserServiceApplication.java`
2. `restaurant-service` → `RestaurantServiceApplication.java`
3. `order-service` → `OrderServiceApplication.java`
4. `notification-service` → `NotificationServiceApplication.java`
5. `delivery-service` → `DeliveryServiceApplication.java`
6. `api-gateway` → `GatewayApplication.java` (run this LAST — it routes to the others)

Each one prints a "started on port XXXX" message when ready.

## Step 3 — test the full flow

All requests go through the gateway on **port 8080** (not the individual service ports).

### 1. Register a user
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "fullName": "Mayank Yadav",
  "email": "mayank@test.com",
  "password": "test1234",
  "phone": "9918050815"
}
```
Copy the `token` from the response — you'll need it for every request below.

### 2. Create a restaurant (direct to restaurant-service — no auth needed for this demo)
```http
POST http://localhost:8082/api/restaurants
Content-Type: application/json

{
  "name": "Punjabi Tadka",
  "cuisine": "North Indian",
  "address": "Hazratganj, Lucknow",
  "latitude": 26.8467,
  "longitude": 80.9462,
  "rating": 4.3
}
```

### 3. Add a menu item to that restaurant (use the id returned above)
```http
POST http://localhost:8082/api/restaurants/1/menu
Content-Type: application/json

{
  "name": "Butter Chicken",
  "description": "Creamy tomato curry",
  "price": 320,
  "category": "Main Course"
}
```

### 4. Place an order (through the gateway — needs the JWT)
```http
POST http://localhost:8080/api/orders
Authorization: Bearer <paste-your-token-here>
Content-Type: application/json

{
  "userId": 1,
  "restaurantId": 1,
  "deliveryAddress": "123 MG Road, Lucknow",
  "items": [
    { "menuItemId": 1, "itemName": "Butter Chicken", "quantity": 2, "price": 320 }
  ]
}
```
Watch the `notification-service` terminal — you'll see `[EMAIL]` and `[SMS]` logs
appear automatically, published via Kafka.

### 5. Move the order through its lifecycle
```http
PATCH http://localhost:8080/api/orders/1/transition
Authorization: Bearer <token>
Content-Type: application/json

{ "event": "CONFIRM_ORDER" }
```
Then `START_PREPARING`, `DISPATCH_ORDER`, `COMPLETE_ORDER` — in that order.
Each transition publishes a new Kafka event, which notification-service picks up instantly.

Try sending `DISPATCH_ORDER` directly after `PLACED` (skipping steps) —
you'll get a `409 Conflict` because the state machine blocks invalid jumps.
**This is the exact behavior to demo in an interview.**

### 6. Check the menu cache (Redis)
```http
GET http://localhost:8082/api/restaurants/1/menu
```
First call → terminal prints `CACHE MISS`. Call it again within 10 minutes →
no log line, because it served straight from Redis.

## Verifying Kafka manually (optional, impressive in interviews)

```
docker exec -it fd-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic order-events --from-beginning
```
This prints every event ever published to that topic, live.

## Things to extend (good next steps for your portfolio)

- Add Spring Security to restaurant-service and delivery-service (currently only gateway + user-service enforce it)
- Replace the mock Email/SMS services with real providers (JavaMailSender, Twilio)
- Add an `ApiResponse<T>` wrapper for consistent response shapes across services
- Write integration tests using Testcontainers for Postgres/Kafka
- Add a Eureka/Consul service registry instead of hardcoded `localhost:PORT` URLs in the gateway

## Stopping everything

```
docker compose down       # stop infra, keep data
docker compose down -v    # stop infra, wipe all data
```
Stop each Spring Boot app with Ctrl+C / the red square in VS Code's Run panel.
# Food_Delivery_Backend
