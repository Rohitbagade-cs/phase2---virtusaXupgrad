package com.example.transaction_service.service;


import com.example.transaction_service.client.AccountClient;
import com.example.transaction_service.client.NotificationClient;
import com.example.transaction_service.model.TransactionRecord;
import com.example.transaction_service.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository txRepo;

    @Mock
    private AccountClient accountClient;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private TransactionService svc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // helper to capture saved tx
    private TransactionRecord captureSavedTx() {
        ArgumentCaptor<TransactionRecord> captor = ArgumentCaptor.forClass(TransactionRecord.class);
        verify(txRepo, atLeastOnce()).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void deposit_success_callsAccountAndNotification_andMarksSuccess() {
        // arrange
        when(txRepo.save(any(TransactionRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        // accountClient.updateBalance returns a Map (simulate success)
        when(accountClient.updateBalance(eq("ACC100"), anyMap()))
                .thenReturn(Map.of("accountNumber","ACC100","balance",1200.0));
        when(notificationClient.sendNotification(anyMap())).thenReturn("SENT");

        // act
        TransactionRecord tx = svc.deposit("ACC100", 200.0);

        // assert
        assertEquals("DEPOSIT", tx.getType());
        assertEquals("SUCCESS", tx.getStatus());
        verify(accountClient, times(1)).updateBalance(eq("ACC100"), anyMap());
        verify(notificationClient, times(1)).sendNotification(anyMap());
        verify(txRepo, atLeastOnce()).save(any(TransactionRecord.class));
    }

    @Test
    void deposit_accountClientThrows_marksFailed() {
        when(txRepo.save(any(TransactionRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        when(accountClient.updateBalance(eq("ACC101"), anyMap())).thenThrow(new RuntimeException("down"));

        TransactionRecord tx = svc.deposit("ACC101", 100.0);

        assertEquals("FAILED", tx.getStatus());
        verify(accountClient, times(1)).updateBalance(eq("ACC101"), anyMap());
        verify(notificationClient, never()).sendNotification(anyMap());
    }

    @Test
    void withdraw_success_marksSuccess_andNotifies() {
        when(txRepo.save(any(TransactionRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        when(accountClient.updateBalance(eq("ACC200"), anyMap())).thenReturn(Map.of("accountNumber","ACC200","balance",300.0));
        when(notificationClient.sendNotification(anyMap())).thenReturn("SENT");

        TransactionRecord tx = svc.withdraw("ACC200", 100.0);

        assertEquals("WITHDRAW", tx.getType());
        assertEquals("SUCCESS", tx.getStatus());
        verify(accountClient).updateBalance(eq("ACC200"), anyMap());
        verify(notificationClient).sendNotification(anyMap());
    }

    @Test
    void transfer_success_callsBothAccounts_andNotifies() {
        when(txRepo.save(any(TransactionRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        // withdraw from source success
        when(accountClient.updateBalance(eq("SRC"), anyMap())).thenReturn(Map.of("accountNumber","SRC","balance",800.0));
        // deposit to dest success
        when(accountClient.updateBalance(eq("DST"), anyMap())).thenReturn(Map.of("accountNumber","DST","balance",1200.0));
        when(notificationClient.sendNotification(anyMap())).thenReturn("SENT");

        TransactionRecord tx = svc.transfer("SRC", "DST", 200.0);

        assertEquals("TRANSFER", tx.getType());
        assertEquals("SUCCESS", tx.getStatus());
        // verify first withdraw then deposit called
        InOrder inOrder = inOrder(accountClient);
        inOrder.verify(accountClient).updateBalance(eq("SRC"), anyMap());
        inOrder.verify(accountClient).updateBalance(eq("DST"), anyMap());
        verify(notificationClient).sendNotification(anyMap());
    }

    @Test
    void transfer_destinationFails_compensatesAndMarksFailed() {
        when(txRepo.save(any(TransactionRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        // withdraw from source success
        when(accountClient.updateBalance(eq("SRC2"), anyMap())).thenReturn(Map.of("accountNumber","SRC2","balance",800.0));
        // deposit to dest fails
        when(accountClient.updateBalance(eq("DST2"), anyMap())).thenThrow(new RuntimeException("dest down"));
        // refund (compensation) should be attempted - simulate success
        when(accountClient.updateBalance(eq("SRC2"), argThat(map -> map.get("amount") != null))).thenReturn(Map.of("accountNumber","SRC2","balance",1000.0));

        TransactionRecord tx = svc.transfer("SRC2", "DST2", 200.0);

        assertEquals("FAILED", tx.getStatus());
        // withdraw called once, deposit called once, refund called at least once
        verify(accountClient, times(1)).updateBalance(eq("SRC2"), anyMap());
        verify(accountClient, times(1)).updateBalance(eq("DST2"), anyMap());
        // compensation attempt (deposit back to source) was performed - at least one more call for refund
        verify(accountClient, atLeast(2)).updateBalance(eq("SRC2"), anyMap());
    }
}

