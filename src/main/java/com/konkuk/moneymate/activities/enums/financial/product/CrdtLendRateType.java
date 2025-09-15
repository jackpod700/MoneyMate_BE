package com.konkuk.moneymate.activities.enums.financial.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CrdtLendRateType {
    대출금리("A"),
    기준금리("B"),
    가산금리("C"),
    가감조정금리("D");

    private final String code;

    public static String fromCode(String code) {
        for (CrdtLendRateType it : CrdtLendRateType.values()) {
            if (it.getCode().equals(code)) {
                return it.name();
            }
        }
        throw new IllegalArgumentException("No matching CrdtLendRateType for code: " + code);
    }
}
