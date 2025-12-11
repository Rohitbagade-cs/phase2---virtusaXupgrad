package com.example.notification_service.service;


import com.example.notification_service.model.Notification;
import com.example.notification_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository repo; // can be null if not using DB

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    // Accepts a generic payload (Map) from other services and processes it
    public Notification process(Map<String,Object> payload) {
        String transactionId = payload.getOrDefault("transactionId", "").toString();
        String type = payload.getOrDefault("type", "INFO").toString();
        Object amount = payload.get("amount");
        String accountNumber = payload.containsKey("accountNumber") ? payload.get("accountNumber").toString() : (payload.containsKey("sourceAccount") ? payload.get("sourceAccount").toString() : null);
        String msg;
        if (amount != null) {
            msg = String.format("%s of amount %s processed for account %s (txn=%s)", type, amount.toString(), accountNumber, transactionId);
        } else {
            msg = String.format("%s notification for account %s (txn=%s)", type, accountNumber, transactionId);
        }
        Notification n = new Notification(transactionId, type, msg, accountNumber, Instant.now());

        // Log to console for demo
        log.info("[NOTIFICATION] {}", msg);

        if (repo != null) {
            try {
                repo.save(n);
            } catch (Exception ex) {
                log.error("Failed to persist notification", ex);
            }
        }
        return n;
    }
}

