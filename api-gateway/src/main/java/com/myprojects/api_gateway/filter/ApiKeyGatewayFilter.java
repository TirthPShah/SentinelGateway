package com.myprojects.api_gateway.filter;

import com.myprojects.api_gateway.model.ApiEvent;
import com.myprojects.api_gateway.service.IpRateLimiterService;
import com.myprojects.api_gateway.service.KafkaProducerService;
import com.myprojects.api_gateway.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ApiKeyGatewayFilter extends AbstractGatewayFilterFactory<Object> {

    private final WebClient.Builder webClientBuilder;
    private final RateLimiterService rateLimiterService;
    private final KafkaProducerService kafkaProducerService;
    private final IpRateLimiterService ipRateLimiterService;

    public static class Config {}

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            if(!path.startsWith("/api")) {
                return chain.filter(exchange);
            }

            long startTime = System.currentTimeMillis();

            String ip = getClientIp(exchange);
            String method = exchange.getRequest().getMethod().name();
            String apiKey = exchange.getRequest().getHeaders().getFirst("x-api-key");

            // IP Rate Limiting
            return ipRateLimiterService.isAllowed(ip)
                    .flatMap(ipAllowed -> {

                        if(!ipAllowed){
                            return reject(exchange, apiKey, path, method, 429, System.currentTimeMillis(), "ip_rate_limited");
                        }

                        if(apiKey == null || apiKey.isBlank()) {
                            return reject(exchange, null, path, method, 401, System.currentTimeMillis(), "missing_api_key");
                        }

                        return webClientBuilder.build()
                                .get()
                                .uri("http://localhost:8081/apikeys/validate?key=" + apiKey)
                                .retrieve()
                                .bodyToMono(Boolean.class)
                                .flatMap(valid -> {

                                    if(!valid) {
                                        return reject(exchange, apiKey, path, method, 401, System.currentTimeMillis(), "invalid_api_key");
                                    }

                                    // API Key Rate Limiting
                                    return rateLimiterService.isAllowed(apiKey)
                                            .flatMap(allowed -> {

                                                if(!allowed) {
                                                    return reject(exchange, apiKey, path, method, 429, System.currentTimeMillis(), "api_rate_limited");
                                                }

                                                long latency = System.currentTimeMillis() - startTime;

                                                kafkaProducerService.sendEvent(ApiEvent.builder()
                                                        .apiKey(apiKey)
                                                        .path(path)
                                                        .method(method)
                                                        .status(200)
                                                        .timestamp(System.currentTimeMillis())
                                                        .latency(latency)
                                                        .message("success")
                                                        .build()
                                                );

                                                return chain.filter(exchange);
                                            });
                                });
                    });
        };
    }

    private Mono<Void> reject(ServerWebExchange exchange,
                              String apiKey,
                              String path,
                              String method,
                              int status,
                              long startTime,
                              String message) {

        long latency = System.currentTimeMillis() - startTime;

        kafkaProducerService.sendEvent(ApiEvent.builder()
                .apiKey(apiKey)
                .path(path)
                .method(method)
                .status(status)
                .timestamp(System.currentTimeMillis())
                .latency(latency)
                .message(message)
                .build()
        );

        exchange.getResponse().setStatusCode(HttpStatus.valueOf(status));
        return exchange.getResponse().setComplete();
    }

    private String getClientIp(ServerWebExchange exchange) {
        String xForwarded = exchange.getRequest().getHeaders().getFirst("x-forwarded-for");
        if(xForwarded != null) {
            return xForwarded.split(",")[0];
        }
        return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }
}
