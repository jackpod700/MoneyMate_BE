package com.konkuk.moneymate.activities.dto;

import com.konkuk.moneymate.activities.enums.TransactionCategory;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumptionStatsResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Long> categoryTotals;
}
