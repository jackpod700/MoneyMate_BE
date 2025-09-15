package com.konkuk.moneymate.activities.enums.financial.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RpayType {
    분할상환방식("D"),
    만기일시상환방식("S");

    private final String code;

    public static String fromCode(String code) {
        for (RpayType it : RpayType.values()) {
            if (it.getCode().equals(code)) {
                return it.name();
            }
        }
        throw new IllegalArgumentException("No matching RpayType for code: " + code);
    }
}
