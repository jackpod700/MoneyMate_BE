package com.konkuk.moneymate.activities.entity;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockPriceHistoryId implements Serializable {

    private String ISIN; // Stock 엔티티의 ID와 타입이 일치해야 합니다.
    private LocalDate date;

}