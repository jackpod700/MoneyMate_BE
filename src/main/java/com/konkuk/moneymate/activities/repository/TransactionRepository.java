package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByBankAccountUidAndTimeBetween(UUID uuid, LocalDateTime start, LocalDateTime end);
}
