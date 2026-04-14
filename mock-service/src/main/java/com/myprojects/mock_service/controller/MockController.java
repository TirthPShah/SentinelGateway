package com.myprojects.mock_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class MockController {

    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of(
                "service", "mock-service",
                "message", "Request reached downstream service."
        );
    }
}
