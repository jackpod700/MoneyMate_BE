package com.konkuk.moneymate.activities.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stock_price_history")
@IdClass(StockPriceHistoryId.class) // 복합 키 클래스를 지정합니다.
public class StockPriceHistory {

    @Id
    @Column(length = 12)
    private String ISIN;

    @Id
    private LocalDate date;

    // 외래 키 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ISIN", insertable = false, updatable = false) // 읽기 전용으로 매핑
    private Stock stock;

    @Column(nullable = false, precision = 20, scale = 6)
    private BigDecimal openPrice;

    @Column(nullable = false, precision = 20, scale = 6)
    private BigDecimal highPrice;

    @Column(nullable = false, precision = 20, scale = 6)
    private BigDecimal lowPrice;

    @Column(nullable = false, precision = 20, scale = 6)
    private BigDecimal endPrice;

}