package com.konkuk.moneymate.activities.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeHistoryId implements Serializable {

    private String baseCurrency;
    private LocalDate date;

}