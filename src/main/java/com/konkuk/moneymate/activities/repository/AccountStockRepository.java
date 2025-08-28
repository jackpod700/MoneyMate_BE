package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.dto.StockHoldingDto;
import com.konkuk.moneymate.activities.entity.AccountStock;
import com.konkuk.moneymate.activities.entity.AccountStockId;
import com.konkuk.moneymate.activities.entity.BankAccount;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.UUID;

public interface AccountStockRepository extends JpaRepository<AccountStock, AccountStockId> {
    @Query("""
        SELECT new com.konkuk.moneymate.activities.dto.StockHoldingDto(
            ba.uid,
            ba.name, 
            s.ISIN, 
            s.name, 
            s.exchangeId, 
            s.ticker, 
            ast.quantity, 
            ast.averagePrice,
            s.currency
        )
        FROM AccountStock ast
        JOIN ast.bankAccount ba
        JOIN ast.stock s
        WHERE ba.user.uid = :userUid
        """)
    List<StockHoldingDto> findAllStockHoldings(@Param("userUid") UUID userUid);
    List<AccountStock> findAccountStocksByBankAccount(BankAccount bankAccount);
}