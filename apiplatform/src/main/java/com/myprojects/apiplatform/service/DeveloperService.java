package com.myprojects.apiplatform.service;

import com.myprojects.apiplatform.model.Developer;
import com.myprojects.apiplatform.repository.DeveloperRepository;
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
