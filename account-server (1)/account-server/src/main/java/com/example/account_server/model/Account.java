package com.example.account_server.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Document(collection = "accounts")
public class Account {

    @Id
    private String id;

    @NotBlank
    private String accountNumber;   // business unique id (eg. ACC1001)

    @NotBlank
    private String holderName;

    @NotNull
    private Double balance = 0.0;

    private boolean active = true;

    // Constructors, getters, setters (or use Lombok)
    public Account() {}

    public Account(String accountNumber, String holderName, Double balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance == null ? 0.0 : balance;
        this.active = true;
    }

    // Getters / Setters ...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

