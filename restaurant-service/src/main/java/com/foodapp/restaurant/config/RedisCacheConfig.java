package com.foodapp.restaurant.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Configures Redis as the cache provider with a 10-minute TTL.
 *
 * Why 10 minutes? Menus change rarely (a restaurant updates its menu maybe
 * once a day), but are READ constantly (every time someone browses).
 * This is the textbook "read-heavy, write-light" caching scenario —
 *
 */
@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper mapper = JsonMapper.builder()
                .activateDefaultTyping(
                        com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.builder()
                                .allowIfBaseType(Object.class).build(),
                        ObjectMapper.DefaultTyping.NON_FINAL
                )
                .build();

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // <-- the TTL strategy
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
