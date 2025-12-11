package com.example.notification_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private String transactionId;
    private String type;          // DEPOSIT, WITHDRAW, TRANSFER, INFO
    private String message;
    private String accountNumber; // primary related account (optional)
    private Instant timestamp;

    public Notification() {}

    public Notification(String transactionId, String type, String message, String accountNumber, Instant timestamp) {
        this.transactionId = transactionId;
        this.type = type;
        this.message = message;
        this.accountNumber = accountNumber;
        this.timestamp = timestamp;
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}

