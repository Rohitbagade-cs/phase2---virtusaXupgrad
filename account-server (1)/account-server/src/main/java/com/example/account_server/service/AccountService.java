package com.example.account_server.service;


import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.account_server.model.Account;
import com.example.account_server.repository.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository repo;

    public AccountService(AccountRepository repo) {
        this.repo = repo;
    }

    public Account createAccount(Account account) {
        if (repo.existsByAccountNumber(account.getAccountNumber())) {
            throw new IllegalArgumentException("Account already exists: " + account.getAccountNumber());
        }
        return repo.save(account);
    }

    public Optional<Account> getByAccountNumber(String accountNumber) {
        return repo.findByAccountNumber(accountNumber);
    }

    @Transactional
    public Account updateBalance(String accountNumber, double delta) {
        Account acc = repo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
        if (!acc.isActive()) {
            throw new IllegalStateException("Account is inactive: " + accountNumber);
        }
        double newBalance = acc.getBalance() + delta;
        if (newBalance < 0) {
            throw new IllegalArgumentException("Insufficient funds. Current balance: " + acc.getBalance());
        }
        acc.setBalance(newBalance);
        return repo.save(acc);
    }

    public Account updateStatus(String accountNumber, boolean active) {
        Account acc = repo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
        acc.setActive(active);
        return repo.save(acc);
    }

}

