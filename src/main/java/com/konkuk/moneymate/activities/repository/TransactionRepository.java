package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.entity.StockTransaction;
import com.konkuk.moneymate.activities.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByBankAccountUidAndTimeBetween(UUID uuid, LocalDateTime start, LocalDateTime end);
    //최신 달이 앞에오도록 정렬
    @Query("""
    SELECT t 
    FROM StockTransaction t 
    JOIN t.stock as st
    WHERE t.bankAccount.uid = :uuid AND t.time BETWEEN :start AND :end
""")

    List<StockTransaction> findStockTransactionByBankAccountUidAndTimeBetween(
            @Param("uuid") UUID uuid,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
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
