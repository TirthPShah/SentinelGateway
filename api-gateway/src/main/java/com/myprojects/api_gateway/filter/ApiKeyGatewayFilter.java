package com.myprojects.api_gateway.filter;

import com.myprojects.api_gateway.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ApiKeyGatewayFilter extends AbstractGatewayFilterFactory<Object> {

    private final WebClient.Builder webClientBuilder;
    private final RateLimiterService rateLimiterService;

    public static class Config {}

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            if(!path.startsWith("/api")) {
                return chain.filter(exchange);
            }

            String apiKey = exchange.getRequest().getHeaders().getFirst("x-api-key");

            if(apiKey == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return rateLimiterService.isAllowed(apiKey)
                    .flatMap(allowed -> {

                        if(!allowed) {
                            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                            return exchange.getResponse().setComplete();
                        }

                        return webClientBuilder.build()
                                .get()
                                .uri("http://localhost:8081/apikeys/validate?key=" + apiKey)
                                .retrieve()
                                .bodyToMono(Boolean.class)
                                .flatMap(valid -> {

                                    if(!valid) {
                                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                        return exchange.getResponse().setComplete();
                                    }

                                    return chain.filter(exchange);
                                });
                    });
        };
    }
}
