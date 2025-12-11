package com.example.account_server.controller;



import java.net.URI;
import java.util.Map;
//import javax.validation.Valid;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.account_server.model.Account;
import com.example.account_server.dto.BalanceUpdateRequest;
import com.example.account_server.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    // Create Account
    @PostMapping
    public ResponseEntity<Account> create(@jakarta.validation.Valid @RequestBody Account account) {
        Account created = service.createAccount(account);
        return ResponseEntity.created(URI.create("/api/accounts/" + created.getAccountNumber())).body(created);
    }

    // Get Account
    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> get(@PathVariable String accountNumber) {
        return service.getByAccountNumber(accountNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update Balance (delta)
    @PutMapping("/{accountNumber}/balance")
    public ResponseEntity<?> updateBalance(@PathVariable String accountNumber,
                                           @jakarta.validation.Valid @RequestBody BalanceUpdateRequest req) {
        try {
            Account updated = service.updateBalance(accountNumber, req.getAmount());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }

    // Activate / Deactivate
    @PutMapping("/{accountNumber}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String accountNumber, @RequestParam boolean active) {
        try {
            Account updated = service.updateStatus(accountNumber, active);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}

