package com.myprojects.analytics_service.repository;

import com.myprojects.analytics_service.model.ApiEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApiEventRepository extends JpaRepository<ApiEventEntity, UUID> {
}
