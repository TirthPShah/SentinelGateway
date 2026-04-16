package com.myprojects.analytics_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myprojects.analytics_service.model.ApiEventEntity;
import com.myprojects.analytics_service.repository.ApiEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ApiEventRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "api-events", groupId = "analytics-group")
    public void consume(String message) {
        try {
            ApiEventEntity event = objectMapper.readValue(message, ApiEventEntity.class);
            repository.save(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
