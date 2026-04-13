package com.myprojects.apiplatform.repository;

import com.myprojects.apiplatform.model.Developer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeveloperRepository
        extends JpaRepository<Developer, UUID> {
}
