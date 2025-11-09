package com.konkuk.moneymate.activities.retirements.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor

public class RetirementSimulateResult {
    private Integer age;
    private Long asset;
    private Long income;
    private Long expense;
}