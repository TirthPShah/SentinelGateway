package com.myprojects.api_gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class IpRateLimiterService {

    private final ReactiveStringRedisTemplate redisTemplate;

    private static final int LIMIT = 50;
    private static final Duration WINDOW = Duration.ofSeconds(10);

    public Mono<Boolean> isAllowed(String ip) {

        String ipKey = "ip:" + ip;

        return redisTemplate.opsForValue()
                .increment(ipKey)
                .flatMap(count -> {
                    if(count == 1) {
                        return redisTemplate.expire(ipKey, WINDOW).thenReturn(true);
                    }
                    return Mono.just(count <= LIMIT);
                }
        );
    }
}
