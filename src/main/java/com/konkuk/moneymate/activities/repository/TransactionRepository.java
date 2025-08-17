package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByBankAccountUidAndTimeBetween(
            UUID bankAccountUid,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("SELECT t.category, SUM(COALESCE(t.income,0) + COALESCE(t.outcome,0)) " +
            "FROM Transaction t " +
            "WHERE t.bankAccount.uid IN :accountUids " +
            "AND t.time BETWEEN :start AND :end " +
            "GROUP BY t.category")
    List<Object[]> sumAmountsByCategory(
            @Param("accountUids") List<UUID> accountUids,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
