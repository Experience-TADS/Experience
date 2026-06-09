package com.senai.experience.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Endpoint de health check — usado pelo AWS ALB e pelo HEALTHCHECK do Docker
 * para verificar se a aplicação está respondendo.
 *
 * Rota pública: GET /health
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString(),
                "service", "Experience API",
                "version", "1.0.0"
        );
    }
}
