package com.konkuk.moneymate.activities.retirements.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


/**
 * <h3>RetirementSimulateDto</h3>
 * <p>
 *  은퇴 시뮬레이터에서 사용할 값
 * </p>
 */

@AllArgsConstructor
@Data
@Builder
public class RetirementSimulateInput {

    private Integer age;                    // 현재 나이 (만 나이)
    private Integer retireAge;              // 은퇴 예정 나이 (예: 55)
    private Long currentAssets;             // 현재 순자산 (자산 - 부채)
    private Long annualIncome;              // 현재 연간 총수입 (월급의 합)
    private Long annualExpense;             // 현재 연간 소비 금액
    private Long pensionPerYear;            // 연금 수령액

    private Integer endAge;                 // 시뮬레이션 종료 시점
    private Double assetReturnRate;         // 연간 자산 수익률 (복리 기준)
    private Double incomeGrowthRate;        // 연 소득 증가율
    private Double inflationRate;           // 연간 인플레이션율
    private Integer pensionStartAge;        // 연금 수령 시작 나이
    private Integer consumptionDropAge;     // 소비 감소 시작 나이
    private Double consumptionDropRate;     // 소비 감소율
    private Integer crashCycle;             // 경기침체 주기 (연 단위)
    private Double crashImpactRate;         // 경기침체 시 자산 손실률
}
