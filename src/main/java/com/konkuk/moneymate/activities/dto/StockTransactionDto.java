package com.konkuk.moneymate.activities.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class StockTransactionDto extends TransactionDto{

    private String ISIN;
    private int quantity;

    public StockTransactionDto(LocalDate trDate, LocalTime trTime, int trOut, int trIn,
                               Long trAfterBalance, String trDest, String ISIN, int quantity) {
        super(trDate, trTime, trOut, trIn, trAfterBalance, trDest);
        this.ISIN = ISIN;
        this.quantity = quantity;
    }
}
