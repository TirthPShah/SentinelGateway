package com.myprojects.apiplatform.controller;

import com.myprojects.apiplatform.model.ApiKey;
import com.myprojects.apiplatform.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
