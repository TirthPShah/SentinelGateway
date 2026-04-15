package com.myprojects.api_gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final RedisScript<Long> tokenBucketScript;

    private static final int CAPACITY = 5;
    private static final double REFILL_RATE = 1.0 / 12000.0;

    public Mono<Boolean> isAllowed(String apiKey) {

        String tokenKey = "tokens:" + apiKey;
        String timeKey = "timestamp:" + apiKey;

        long now = System.currentTimeMillis();

        return redisTemplate.execute(
                        tokenBucketScript,
                        List.of(tokenKey, timeKey),
                        String.valueOf(CAPACITY),
                        String.valueOf(REFILL_RATE),
                        String.valueOf(now)
                )
                .next()
                .map(result -> result == 1);
    }
}