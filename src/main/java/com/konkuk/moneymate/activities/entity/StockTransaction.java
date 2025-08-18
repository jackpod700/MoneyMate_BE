package com.konkuk.moneymate.activities.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Entity
public class StockTransaction extends Transaction {
    // 주식 거래를 나타내는 엔티티

    @ManyToOne
    private Stock stock; // 주식 티커

    @Column(name = "quantity", nullable = false)
    private int quantity; // 거래 수량

    public StockTransaction() {
        super();
    }
    public StockTransaction(BankAccount bankAccount, String counterAccount, Integer outcome, Integer income, String category, LocalDateTime time, Long afterBalance,Stock stock, int quantity) {
        super(bankAccount, counterAccount, outcome, income, category, time, afterBalance);
        this.stock = stock;
        this.quantity = quantity;
    }
}
