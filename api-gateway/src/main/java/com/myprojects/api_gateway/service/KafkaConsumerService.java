package com.myprojects.api_gateway.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service

public class KafkaConsumerService {

    @KafkaListener(topics = "api-events", groupId = "analytics-group")
    public void consume(String message) {
        System.out.println("📩 EVENT: " + message);
    }
}
