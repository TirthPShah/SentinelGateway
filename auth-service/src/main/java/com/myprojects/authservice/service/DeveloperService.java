package com.myprojects.authservice.service;

import com.myprojects.authservice.model.Developer;
import com.myprojects.authservice.repository.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DeveloperService {

    private final DeveloperRepository developerRepository;

    public Developer createDeveloper(String name, String email) {

        Developer developer = Developer.builder()
                .name(name)
                .email(email)
                .createdAt(Instant.now())
                .build();

        return developerRepository.save(developer);
    }
}
