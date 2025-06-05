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
            /**
             * income before retirement
             */
            if (age < retireAge) {
                income = (long) (income * (1 + incomeGrowthRate));
            } else {
                income = 0L;
            }

            if (age >= pensionStartAge) {
                income += pension;
            }

            /**
             *
             */
            if (age == consumptionDropAge) {
                expense = (long) (expense * (1 - consumptionDropRate));
            }
            expense = (long) (expense * (1 + inflationRate));

            asset = asset + income - expense;

            /**
             * ressession year
             */
            boolean isRecessionYear = ((age - currentAge) % crashCycle == 0 && age != currentAge);
            if (isRecessionYear) {
                asset = (long) (asset * (1 - crashImpactRate));
            }

            /**
             *
             */
            asset = (long) (asset * (1 + assetReturnRate));

            /**
             *  asset < 0 ?
             */
            if (asset <= 0) {
                resultList.add(RetirementSimulateResult.builder()
                        .age(age)
                        .asset(0L)
                        .income(income)
                        .expense(expense)
                        .build());
                break; // 자산 고갈 시 종료
            }

            /**
             * result set
             */
            resultList.add(RetirementSimulateResult.builder()
                    .age(age)
                    .asset(asset)
                    .income(income)
                    .expense(expense)
                    .build());
        }




        return resultList;
    }


}
