package com.example.account_server.dto;


import jakarta.validation.constraints.NotNull;

public class BalanceUpdateRequest {
    @NotNull
    private Double amount; // positive or negative delta

    public BalanceUpdateRequest() {}
    public BalanceUpdateRequest(Double amount) { this.amount = amount; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
