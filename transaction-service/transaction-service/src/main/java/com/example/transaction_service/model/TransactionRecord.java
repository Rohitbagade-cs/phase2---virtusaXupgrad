package com.example.transaction_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "transactions")
public class TransactionRecord {
    @Id
    private String id;
    private String transactionId; // e.g. TXN-2025001
    private String type; // DEPOSIT, WITHDRAW, TRANSFER
    private Double amount;
    private String sourceAccount;
    private String destinationAccount;
    private String status; // SUCCESS, FAILED
    private Instant timestamp;

    public TransactionRecord() { }

    public TransactionRecord(String transactionId, String type, Double amount, String sourceAccount, String destinationAccount, String status, Instant timestamp) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.status = status;
        this.timestamp = timestamp;
    }

    // getters & setters omitted for brevity (or use Lombok)
    // ... generate getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getSourceAccount() { return sourceAccount; }
    public void setSourceAccount(String sourceAccount) { this.sourceAccount = sourceAccount; }
    public String getDestinationAccount() { return destinationAccount; }
    public void setDestinationAccount(String destinationAccount) { this.destinationAccount = destinationAccount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}

