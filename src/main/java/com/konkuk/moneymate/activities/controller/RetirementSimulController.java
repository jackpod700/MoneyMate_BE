package com.konkuk.moneymate.activities.controller;

import com.konkuk.moneymate.activities.dto.RetirementSimulateInput;
import com.konkuk.moneymate.activities.dto.RetirementSimulateResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * <h3> class : RetirementSimulController </h3>
 * <p> 로직 수정중으로 아직 테스트하지 말아주세요 </p>
 *
 *
 *
 */

@RestController
public class RetirementSimulController {

    @PostMapping("/asset/retirement/simulate")
    public List<RetirementSimulateResult> retirementSimul(@RequestBody RetirementSimulateInput input) {

        List<RetirementSimulateResult> resultList = new ArrayList<>();

        int currentAge = input.getAge(); // 현재 나이 (만 나이)
        int retireAge = input.getRetireAge();
        long asset = input.getCurrentAssets();
        long income = input.getAnnualIncome();
        long expense = input.getAnnualExpense();
        long pension = input.getPensionPerYear();

        int endAge = input.getEndAge();
        double assetReturnRate = input.getAssetReturnRate();
        double incomeGrowthRate = input.getIncomeGrowthRate();
        double inflationRate = input.getInflationRate();
        double consumptionDropRate = input.getConsumptionDropRate();
        int consumptionDropAge = input.getConsumptionDropAge();
        int pensionStartAge = input.getPensionStartAge();

        int crashCycle = input.getCrashCycle();
        double crashImpactRate = input.getCrashImpactRate();



        for (int age = currentAge; age <= endAge; age++) {
            boolean isRecessionYear = ((age - currentAge) % crashCycle == 0 && age != currentAge);
            double effectiveReturnRate = isRecessionYear ? crashImpactRate : assetReturnRate;

            asset = (long) (asset * (1 + effectiveReturnRate));

            if (age < retireAge) {
                income = (long) (income * (1 + incomeGrowthRate));
            } else {

                if (age >= pensionStartAge) {
                    income = pension;
                } else {
                    income = 0L;
                }
            }

            //  소비
            long adjustedExpense = (long) (expense * (1 + inflationRate));
            if (age >= consumptionDropAge) {
                adjustedExpense = (long) (adjustedExpense * (1 - consumptionDropRate));
            }
            
            asset = asset + income - adjustedExpense;

            // result
            resultList.add(RetirementSimulateResult.builder()
                    .age(age)
                    .asset(asset)
                    .income(income)
                    .expense(adjustedExpense)
                    .build());

            // 소비 갱신
            expense = adjustedExpense;

            // 예외 케이스
            if (asset <= 0) {
                break;
            }
        }

        return resultList;
    }


}
