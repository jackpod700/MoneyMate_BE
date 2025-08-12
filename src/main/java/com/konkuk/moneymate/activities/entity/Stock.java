package com.konkuk.moneymate.activities.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock")
public class Stock {
    @Id
    @Column(length = 12)
    private String ISIN;

    @Column(name = "exchange_id", nullable = false)
    private String exchangeId;

    @Column(nullable = false)
    private String ticker;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String currency;
}