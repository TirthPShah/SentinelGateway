package com.myprojects.authservice.service;

import com.myprojects.authservice.model.ApiKey;
import com.myprojects.authservice.repository.ApiKeyRepository;
import com.myprojects.authservice.repository.DeveloperRepository;
import com.myprojects.authservice.util.ApiKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyGenerator apiKeyGenerator;
    private final DeveloperRepository developerRepository;

    public ApiKey generateKey(UUID developerId) {

        if(!developerRepository.existsById(developerId)) {
            throw new RuntimeException("Resource not found");
        }

        ApiKey apiKey = ApiKey.builder()
                .key(apiKeyGenerator.generateKey())
                .developerId(developerId)
                .rateLimit(100)
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        return apiKeyRepository.save(apiKey);

    }

    public boolean validateKey(String key) {

        Optional<ApiKey> apiKey = apiKeyRepository.findByKey(key);

        System.out.println("FOUND IN DB: " + apiKey.isPresent());

        return apiKey
                .filter(k -> !k.getRevoked())
                .isPresent();
    }
}
