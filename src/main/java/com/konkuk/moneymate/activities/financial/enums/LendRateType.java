package com.konkuk.moneymate.activities.financial.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LendRateType {
    변동금리("C"),
    고정금리("F");

    private final String code;

    public static String fromCode(String code) {
        for (LendRateType it : LendRateType.values()) {
            if (it.getCode().equals(code)) {
                return it.name();
            }
        }
        throw new IllegalArgumentException("No matching RpayType for code: " + code);
    }
}
