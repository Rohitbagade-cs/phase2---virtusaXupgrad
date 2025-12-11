package com.example.account_server.service;


import com.example.account_server.model.Account;
import com.example.account_server.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository repo;

    @InjectMocks
    private AccountService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccount_success() {
        Account a = new Account("ACC100", "Rohit", 1000.0);
        when(repo.existsByAccountNumber("ACC100")).thenReturn(false);
        when(repo.save(any(Account.class))).thenReturn(a);

        Account created = service.createAccount(a);

        assertNotNull(created);
        assertEquals("ACC100", created.getAccountNumber());
        verify(repo, times(1)).save(a);
    }

    @Test
    void createAccount_duplicate_throws() {
        Account a = new Account("ACC100", "Rohit", 1000.0);
        when(repo.existsByAccountNumber("ACC100")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.createAccount(a));
        assertTrue(ex.getMessage().contains("Account already exists"));
        verify(repo, never()).save(any());
    }

    @Test
    void updateBalance_deposit_success() {
        Account a = new Account("ACC200","User",500.0);
        when(repo.findByAccountNumber("ACC200")).thenReturn(Optional.of(a));
        when(repo.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account updated = service.updateBalance("ACC200", 200.0);

        assertEquals(700.0, updated.getBalance());
        verify(repo).save(any(Account.class));
    }

    @Test
    void updateBalance_withdraw_insufficient_throws() {
        Account a = new Account("ACC300","User",50.0);
        when(repo.findByAccountNumber("ACC300")).thenReturn(Optional.of(a));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateBalance("ACC300", -100.0));
        assertTrue(ex.getMessage().toLowerCase().contains("insufficient"));
        verify(repo, never()).save(any());
    }

    @Test
    void updateBalance_inactiveAccount_throws() {
        Account a = new Account("ACC400","User",500.0);
        a.setActive(false);
        when(repo.findByAccountNumber("ACC400")).thenReturn(Optional.of(a));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.updateBalance("ACC400", 100.0));
        assertTrue(ex.getMessage().toLowerCase().contains("inactive"));
        verify(repo, never()).save(any());
    }
}

