package com.konkuk.moneymate.activities.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exchange_history")
@IdClass(ExchangeHistoryId.class) // 복합 키 클래스 지정
public class ExchangeHistory {

    @Id
    @Column(name = "base_currency", length = 3, nullable = false)
    private String baseCurrency;

    @Id
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "open_price", nullable = false, precision = 20, scale = 6)
    private BigDecimal openPrice;

    @Column(name = "high_price", nullable = false, precision = 20, scale = 6)
    private BigDecimal highPrice;

    @Column(name = "low_price", nullable = false, precision = 20, scale = 6)
    private BigDecimal lowPrice;

    @Column(name = "end_price", nullable = false, precision = 20, scale = 6)
    private BigDecimal endPrice;

}