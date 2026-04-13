package com.myprojects.authservice.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ApiKeyGenerator {

    public String generateKey() {
        return "sk_live_" + UUID.randomUUID().toString().replaceAll("-", "");
    }
}
