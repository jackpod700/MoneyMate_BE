package com.konkuk.moneymate.activities.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "account_stock")
@IdClass(AccountStockId.class) // 복합키
public class AccountStock {
    @Id
    @ManyToOne
    @JoinColumn(name = "bank_account_uid", nullable = false)
    private BankAccount bankAccount;

    @Id
    @ManyToOne
    @JoinColumn(name = "ISIN", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "average_price", nullable = false, precision = 20, scale = 6)
    private BigDecimal averagePrice;
}