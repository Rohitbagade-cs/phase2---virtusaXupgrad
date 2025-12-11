package com.example.notification_service.controller;

import com.example.notification_service.model.Notification;
import com.example.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService svc;

    public NotificationController(NotificationService svc) {
        this.svc = svc;
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(@Valid @RequestBody Map<String,Object> payload) {
        try {
            Notification n = svc.process(payload);
            return ResponseEntity.ok(Map.of("status", "SENT", "id", n.getId(), "message", n.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("status", "FAILED", "error", ex.getMessage()));
        }
    }

    // Simple test endpoint
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("notification-service:ok");
    }
}

