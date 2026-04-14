package com.myprojects.authservice.controller;

import com.myprojects.authservice.model.ApiKey;
import com.myprojects.authservice.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/apikeys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ApiKey generateKey(@RequestParam UUID developerId) {
        return apiKeyService.generateKey(developerId);
    }

    @GetMapping("/validate")
    public boolean validateKey(@RequestParam String key) {
        return apiKeyService.validateKey(key);
    }
}
