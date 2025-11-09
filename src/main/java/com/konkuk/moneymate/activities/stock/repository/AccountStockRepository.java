package com.konkuk.moneymate.activities.stock.repository;

import com.konkuk.moneymate.activities.bankaccount.entity.AccountStock;
import com.konkuk.moneymate.activities.bankaccount.dto.AccountStockId;
import com.konkuk.moneymate.activities.bankaccount.entity.BankAccount;
import java.util.List;

import com.konkuk.moneymate.activities.stock.dto.StockHoldingDto;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.UUID;

public interface AccountStockRepository extends JpaRepository<AccountStock, AccountStockId> {
    @Query("""
        SELECT new com.konkuk.moneymate.activities.stock.dto.StockHoldingDto(
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