package com.konkuk.moneymate.activities.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TransactionDto {
    private LocalDate trDate;
    private LocalTime trTime;
    private int trOut;
    private int trIn;
    private int trAfterBalance;
    private String trDest;
}
