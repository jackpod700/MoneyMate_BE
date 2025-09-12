package com.konkuk.moneymate.activities.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
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
}
