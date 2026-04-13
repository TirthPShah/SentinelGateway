package com.myprojects.apiplatform.repository;

import com.myprojects.apiplatform.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyRepository
        extends JpaRepository<ApiKey, Long> {

    Optional<ApiKey> findByKey(String key);
}
