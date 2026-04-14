package com.myprojects.api_gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final ReactiveStringRedisTemplate redisTemplate;

    private static final int CAPACITY = 5;
    private static final double REFILL_RATE = 1.0 / 12.0;

    public Mono<Boolean> isAllowed(String apiKey) {

        String tokenKey = "tokens:" + apiKey;
        String timeKey = "timestamp:" + apiKey;

        long now = System.currentTimeMillis();

        return redisTemplate.opsForValue()
                .get(tokenKey)
                .defaultIfEmpty(String.valueOf(CAPACITY))
                .flatMap(tokenStr -> {

                    double tokens = Double.parseDouble(tokenStr);

                    return redisTemplate.opsForValue()
                            .get(timeKey)
                            .defaultIfEmpty(String.valueOf(now))
                            .flatMap(lastRefillStr -> {

                                long lastRefill = Long.parseLong(lastRefillStr);

                                double elapsedSeconds = (now - lastRefill) / 1000.0;
                                double newTokens = tokens + elapsedSeconds *  REFILL_RATE;

                                if(newTokens >= CAPACITY) {
                                    newTokens = CAPACITY;
                                }

                                if(newTokens < 1) {
                                    return Mono.just(false);
                                }

                                double remainingTokens = newTokens - 1;

                                return redisTemplate.opsForValue()
                                        .set(tokenKey, String.valueOf(remainingTokens))
                                        .then(redisTemplate.opsForValue()
                                                .set(timeKey, String.valueOf(now)))
                                        .thenReturn(true);
                            });
                });
    }
}
