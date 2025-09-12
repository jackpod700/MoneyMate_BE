package com.konkuk.moneymate.activities.enums.financialProduct;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InterestType {
    단리("S"),
    복리("M");

    private final String code;

    public static String fromCode(String code) {
        for (InterestType it : InterestType.values()) {
            if (it.getCode().equals(code)) {
                return it.name();
            }
        }
        throw new IllegalArgumentException("No matching InterestType for code: " + code);
    }
}
