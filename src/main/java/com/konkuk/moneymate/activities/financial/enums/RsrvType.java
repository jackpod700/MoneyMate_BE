package com.konkuk.moneymate.activities.financial.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RsrvType {
    정액적립식("S"),
    자유적립식("F");

    private final String code;

    public static String fromCode(String code) {
        for (RsrvType it : RsrvType.values()) {
            if (it.getCode().equals(code)) {
                return it.name();
            }
        }
        throw new IllegalArgumentException("No matching RsrvType for code: " + code);
    }
}
