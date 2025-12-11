package com.example.transaction_service.service;


import com.example.transaction_service.client.AccountClient;
import com.example.transaction_service.client.NotificationClient;
import com.example.transaction_service.model.TransactionRecord;
import com.example.transaction_service.repository.TransactionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository txRepo;
    private final AccountClient accountClient;
    private final NotificationClient notificationClient;

    public TransactionService(TransactionRepository txRepo, AccountClient accountClient, NotificationClient notificationClient) {
        this.txRepo = txRepo;
        this.accountClient = accountClient;
        this.notificationClient = notificationClient;
    }

    private String newTxnId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }

    public TransactionRecord record(TransactionRecord tx) {
        tx.setTimestamp(Instant.now());
        return txRepo.save(tx);
    }

    // deposit: add amount to account
    @CircuitBreaker(name = "account", fallbackMethod = "depositFallback")
    public TransactionRecord deposit(String accountNumber, Double amount) {
        String txnId = newTxnId();
        TransactionRecord tx = new TransactionRecord(txnId, "DEPOSIT", amount, null, accountNumber, "PENDING", Instant.now());
        tx = record(tx);

        Map<String, Double> body = new HashMap<>();
        body.put("amount", Math.abs(amount)); // deposit is positive
        try {
            accountClient.updateBalance(accountNumber, body); // may throw FeignException
            tx.setStatus("SUCCESS");
            record(tx);
            // notify
            Map<String,Object> notify = new HashMap<>();
            notify.put("accountNumber", accountNumber);
            notify.put("transactionId", txnId);
            notify.put("type", "DEPOSIT");
            notify.put("amount", amount);
            notificationClient.sendNotification(notify);
        } catch (Exception ex) {
            tx.setStatus("FAILED");
            record(tx);
        }
        return tx;
    }

    public TransactionRecord depositFallback(String accountNumber, Double amount, Throwable t) {
        TransactionRecord tx = new TransactionRecord(newTxnId(), "DEPOSIT", amount, null, accountNumber, "FAILED", Instant.now());
        record(tx);
        return tx;
    }

    // withdraw: subtract amount (amount passed positive, we send negative delta)
    @CircuitBreaker(name = "account", fallbackMethod = "withdrawFallback")
    public TransactionRecord withdraw(String accountNumber, Double amount) {
        String txnId = newTxnId();
        TransactionRecord tx = new TransactionRecord(txnId, "WITHDRAW", amount, accountNumber, null, "PENDING", Instant.now());
        tx = record(tx);

        Map<String, Double> body = new HashMap<>();
        body.put("amount", -Math.abs(amount)); // negative delta for withdrawal
        try {
            accountClient.updateBalance(accountNumber, body);
            tx.setStatus("SUCCESS");
            record(tx);
            Map<String,Object> notify = new HashMap<>();
            notify.put("accountNumber", accountNumber);
            notify.put("transactionId", txnId);
            notify.put("type", "WITHDRAW");
            notify.put("amount", amount);
            notificationClient.sendNotification(notify);
        } catch (Exception ex) {
            tx.setStatus("FAILED");
            record(tx);
        }
        return tx;
    }

    public TransactionRecord withdrawFallback(String accountNumber, Double amount, Throwable t) {
        TransactionRecord tx = new TransactionRecord(newTxnId(), "WITHDRAW", amount, accountNumber, null, "FAILED", Instant.now());
        record(tx);
        return tx;
    }

    // transfer: withdraw from source, deposit to destination, simple orchestration (not a full saga)
    @CircuitBreaker(name = "account", fallbackMethod = "transferFallback")
    public TransactionRecord transfer(String sourceAccount, String destinationAccount, Double amount) {
        String txnId = newTxnId();
        TransactionRecord tx = new TransactionRecord(txnId, "TRANSFER", amount, sourceAccount, destinationAccount, "PENDING", Instant.now());
        tx = record(tx);

        // 1) withdraw from source
        Map<String, Double> withdrawBody = new HashMap<>();
        withdrawBody.put("amount", -Math.abs(amount));
        try {
            accountClient.updateBalance(sourceAccount, withdrawBody);
        } catch (Exception ex) {
            tx.setStatus("FAILED");
            record(tx);
            return tx;
        }

        // 2) deposit to destination
        Map<String, Double> depositBody = new HashMap<>();
        depositBody.put("amount", Math.abs(amount));
        try {
            accountClient.updateBalance(destinationAccount, depositBody);
        } catch (Exception ex) {
            // compensation: refund source by depositing back
            Map<String, Double> refund = new HashMap<>();
            refund.put("amount", Math.abs(amount));
            try {
                accountClient.updateBalance(sourceAccount, refund);
            } catch (Exception compEx) {
                // log compensation failure; tx remains FAILED
            }
            tx.setStatus("FAILED");
            record(tx);
            return tx;
        }

        // success
        tx.setStatus("SUCCESS");
        record(tx);
        // notify both accounts
        Map<String,Object> notify = new HashMap<>();
        notify.put("sourceAccount", sourceAccount);
        notify.put("destinationAccount", destinationAccount);
        notify.put("transactionId", txnId);
        notify.put("type", "TRANSFER");
        notify.put("amount", amount);
        notificationClient.sendNotification(notify);

        return tx;
    }

    public TransactionRecord transferFallback(String sourceAccount, String destinationAccount, Double amount, Throwable t) {
        TransactionRecord tx = new TransactionRecord(newTxnId(), "TRANSFER", amount, sourceAccount, destinationAccount, "FAILED", Instant.now());
        record(tx);
        return tx;
    }
}

