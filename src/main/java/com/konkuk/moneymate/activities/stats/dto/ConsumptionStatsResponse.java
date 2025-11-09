package com.konkuk.moneymate.activities.stats.dto;

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
