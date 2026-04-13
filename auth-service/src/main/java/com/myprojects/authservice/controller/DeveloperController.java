package com.myprojects.authservice.controller;

import com.myprojects.authservice.model.Developer;
import com.myprojects.authservice.service.DeveloperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/developers")
@RequiredArgsConstructor
public class DeveloperController {

    private final DeveloperService developerService;

    @PostMapping
    public Developer createDeveloper(@RequestBody Developer developer) {
        return developerService.createDeveloper(
                developer.getName(),
                developer.getEmail()
        );
    }
}
