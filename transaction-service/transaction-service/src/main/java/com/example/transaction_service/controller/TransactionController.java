package com.example.transaction_service.controller;

import com.example.transaction_service.model.TransactionRecord;
import com.example.transaction_service.service.TransactionService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService svc;

    public TransactionController(TransactionService svc) { this.svc = svc; }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionRecord> deposit(@RequestBody Map<String,Object> body) {
        String account = (String) body.get("accountNumber");
        Double amount = Double.valueOf(body.get("amount").toString());
        TransactionRecord tx = svc.deposit(account, amount);
        return ResponseEntity.ok(tx);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionRecord> withdraw(@RequestBody Map<String,Object> body) {
        String account = (String) body.get("accountNumber");
        Double amount = Double.valueOf(body.get("amount").toString());
        TransactionRecord tx = svc.withdraw(account, amount);
        return ResponseEntity.ok(tx);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionRecord> transfer(@RequestBody Map<String,Object> body) {
        String src = (String) body.get("sourceAccount");
        String dst = (String) body.get("destinationAccount");
        Double amount = Double.valueOf(body.get("amount").toString());
        TransactionRecord tx = svc.transfer(src, dst, amount);
        return ResponseEntity.ok(tx);
    }
}

