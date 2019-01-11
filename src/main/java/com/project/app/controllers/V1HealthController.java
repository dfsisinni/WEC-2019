package com.project.app.controllers;

import com.google.common.collect.ImmutableMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class V1HealthController {

    private static final Map HEALTHY = ImmutableMap.of("status", "healthy");

    @GetMapping
    public Map<String, String> getHealth() {
        return HEALTHY;
    }
}
