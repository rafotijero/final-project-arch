package com.ecommerce.auth.presentation.controller;

import com.ecommerce.auth.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "auth-service");

        return ResponseEntity.ok(health);
    }

    @GetMapping("/db")
    public ResponseEntity<Map<String, Object>> databaseHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("timestamp", LocalDateTime.now());

        try {
            long userCount = userRepository.count();
            health.put("status", "UP");
            health.put("database", "Connected");
            health.put("userCount", userCount);
            log.info("Database health check: OK, users count: {}", userCount);
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("database", "Disconnected");
            health.put("error", e.getMessage());
            log.error("Database health check failed", e);
            return ResponseEntity.status(503).body(health);
        }

        return ResponseEntity.ok(health);
    }
}
