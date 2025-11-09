package com.konkuk.moneymate.activities.financial.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FinancialGroupCode {
    은행("bank","020000"),
    저축은행("savingsBank","030300"),
    보험("insurance","050000"),
    여신전문("specializedCredit","030200");


    private final String requestCode;
    private final String code;

    public static String fromRequestCodeToCode(String requestCode) {
        for (FinancialGroupCode fg : FinancialGroupCode.values()) {
            if (fg.getRequestCode().equals(requestCode)) {
                return fg.getCode();
            }
        }
        throw new IllegalArgumentException("No matching FinancialGroupCode for code: " + requestCode);
    }
}
