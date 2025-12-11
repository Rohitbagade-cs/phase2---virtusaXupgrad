package com.example.transaction_service.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.transaction_service.model.TransactionRecord;

import java.util.List;

public interface TransactionRepository extends MongoRepository<TransactionRecord, String> {
    List<TransactionRecord> findBySourceAccount(String accountNumber);
    List<TransactionRecord> findByDestinationAccount(String accountNumber);
}

