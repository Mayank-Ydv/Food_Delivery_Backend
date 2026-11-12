package com.foodapp.gateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

/**
 * Runs BEFORE every request to a protected route, checks for a valid JWT
 * in the Authorization header, and rejects the request with 401 if missing
 * or invalid — before it ever reaches a downstream microservice.
 *
 * This means individual services (order-service, restaurant-service etc)
 * don't need to re-implement auth checking — the gateway is the single
 * choke point. Downstream services trust requests that reach them.
 */
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B59";

    // Routes that don't require a token
    private static final List<String> OPEN_PATHS = List.of(
            "/api/auth/register",
            "/api/auth/login"
    );

    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            if (OPEN_PATHS.stream().anyMatch(path::startsWith)) {
                return chain.filter(exchange); // skip auth check
            }

            if (!request.getHeaders().containsKey("Authorization")) {
                return onError(exchange, "Missing Authorization header");
            }

            String authHeader = request.getHeaders().getFirst("Authorization");
            String token = authHeader != null && authHeader.startsWith("Bearer ")
                    ? authHeader.substring(7)
                    : null;

            if (token == null) {
                return onError(exchange, "Invalid Authorization header format");
            }

            try {
                SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
                Jwts.parser().verifyWith(key).build().parseSignedClaims(token); // throws if invalid/expired
            } catch (Exception e) {
                return onError(exchange, "Invalid or expired token");
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // marker class — no config properties needed for this filter
    }
}
