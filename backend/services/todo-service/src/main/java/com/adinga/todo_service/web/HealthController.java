package com.adinga.todoservice.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthController {
    @GetMapping("/api/todos/health")
    public Map<String, Object> health() {
        return Map.of("status", "OK");
    }
}
