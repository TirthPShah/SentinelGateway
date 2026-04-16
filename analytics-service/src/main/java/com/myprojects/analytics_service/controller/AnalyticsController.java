package com.myprojects.analytics_service.controller;

import com.myprojects.analytics_service.model.ApiEventEntity;
import com.myprojects.analytics_service.repository.ApiEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final ApiEventRepository repository;

    @GetMapping("/events")
    public List<ApiEventEntity> getAll() {
        return repository.findAll();
    }
}
