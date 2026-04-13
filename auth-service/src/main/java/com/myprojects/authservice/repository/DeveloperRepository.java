package com.myprojects.authservice.repository;

import com.myprojects.authservice.model.Developer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeveloperRepository
        extends JpaRepository<Developer, UUID> {
}
