package com.konkuk.moneymate.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


/**
 * <h3>RetirementSimulateDto</h3>
 * <p>
 *
 * </p>
 */

@AllArgsConstructor
@Data
@Builder
public class RetirementSimulateInput {

    private Integer age;                    // 유저의 나이 (만 나이)
    private Integer retireAge;              // 은퇴 예정 나이 (예: 55)
    private Long currentAssets;             // 현재 순자산 (자산 - 부채)
    private Long annualIncome;              // 현재 연간 총수입 (월급의 합)
    private Long annualExpense;             // 현재 연간 소비 금액
    private Long pensionPerYear;            // 연금 수령액


    private Integer endAge = 90;                 // 시뮬레이션 종료 시점 (예: 90)

    private Double assetReturnRate = 0.07;         // 연간 자산 수익률 (복리 기준)
    private Double incomeGrowthRate = 0.04;        // 연 소득 증가율
    private Double inflationRate = 0.02;           // 연간 인플레이션율

    private Integer pensionStartAge = 60;        // 연금 수령 시작 나이 (예: 60)

    private Integer consumptionDropAge = 70;     // 소비 감소 시작 나이 (예: 70)
    private Double consumptionDropRate = -(0.2);     // 소비 감소율 (예: -0.2)

    private Integer crashCycle = 6;             // 경기침체 주기 (연 단위, 예: 6)
    private Double crashImpactRate = -(0.1);         // 경기침체 시 자산 손실률 (예: -0.1)
}
