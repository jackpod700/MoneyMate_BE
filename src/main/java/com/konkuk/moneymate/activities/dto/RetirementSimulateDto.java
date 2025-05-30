package com.konkuk.moneymate.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@AllArgsConstructor
@Data
@Builder
public class RetirementSimulateDto {

    Integer age;
    Integer retireAge;
    Long currentAssets;
    Long annualIncome;
    Long annualExpense;
    Long pensionPerYear;

}
