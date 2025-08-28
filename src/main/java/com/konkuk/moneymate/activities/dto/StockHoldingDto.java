package com.konkuk.moneymate.activities.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockHoldingDto {
    private UUID accountUid;
    private String accountName;
    private String ISIN;
    private String stockName;
    private String exchangeId;
    private String ticker;
    private String currency;
    private Integer quantity;
    private BigDecimal averagePrice;
    private BigDecimal currentTotalPrice; // 실시간 API로 채움
    private BigDecimal profit;            // 실시간 API로 채움

    public StockHoldingDto(UUID accountUid, String accountName, String ISIN,
                           String stockName, String exchangeId, String ticker,
                           Integer quantity, BigDecimal averagePrice, String currency) {
        this.accountUid = accountUid;
        this.accountName = accountName;
        this.ISIN = ISIN;
        this.stockName = stockName;
        this.exchangeId = exchangeId;
        this.ticker = ticker;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.currency = currency;
    }
}