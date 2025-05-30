package com.konkuk.moneymate.activities.controller;

import com.konkuk.moneymate.activities.dto.RetirementSimulateInput;
import com.konkuk.moneymate.activities.dto.RetirementSimulateResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * <h3> class : RetirementSimulController </h3>
 * <p> 로직 수정중으로 임시로 비웠습니다 </p>
 */

@RestController
public class RetirementSimulController {

    @PostMapping("/asset/retirement/simulate")
    public List<RetirementSimulateResult> retirementSimul(@RequestBody RetirementSimulateInput dto) {

        List<RetirementSimulateResult> resultList = new ArrayList<>();

        // init
        int currentAge = dto.getAge();
        int retireAge = dto.getRetireAge();
        int endAge = dto.getEndAge();

        long asset = dto.getCurrentAssets();
        long income = dto.getAnnualIncome();
        long expense = dto.getAnnualExpense();
        long pension = dto.getPensionPerYear();

        double assetReturnRate = dto.getAssetReturnRate();
        double incomeGrowthRate = dto.getIncomeGrowthRate();
        double inflationRate = dto.getInflationRate();
        double consumptionDropRate = dto.getConsumptionDropRate();
        int consumptionDropAge = dto.getConsumptionDropAge();
        int pensionStartAge = dto.getPensionStartAge();

        int crashCycle = dto.getCrashCycle();
        double crashImpactRate = dto.getCrashImpactRate();

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
                adjustedExpense = (long) (adjustedExpense * (1 + consumptionDropRate));
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
