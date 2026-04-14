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

    private static final int LIMIT = 5;
    private static final int WINDOW_SECONDS = 60;

    public Mono<Boolean> isAllowed(String apiKey) {

        String key = "rate_limit:" + apiKey;

        return redisTemplate.opsForValue()
                .increment(key)
                .flatMap(count -> {

                    if(count == 1) {
                        return redisTemplate
                                .expire(key, Duration.ofSeconds(WINDOW_SECONDS))
                                .thenReturn(true);
                    }

                    if(count <= LIMIT) {
                        return Mono.just(true);
                    }

                    return Mono.just(false);
                });
    }
}
