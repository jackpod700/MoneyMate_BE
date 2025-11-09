package com.konkuk.moneymate.activities.stock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;

@Entity
@Getter
@Table(name = "stock")
public class Stock {
    @Id
    @Column(length = 12)
    private String ISIN;

    @OneToMany(mappedBy = "stock")
    @JsonIgnore
    private List<StockTransaction> stockTransactions;

    @Column(name = "exchange_id", nullable = false)
    private String exchangeId;

    @Column(nullable = false)
    private String ticker;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String currency;

    public Stock(String ticker, String name) {
        this.ticker = ticker;
        this.name = name;
    }

    public Stock(){
    }
}