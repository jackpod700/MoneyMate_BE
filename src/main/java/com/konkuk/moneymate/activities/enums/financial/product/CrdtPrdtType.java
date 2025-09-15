package com.konkuk.moneymate.activities.enums.financial.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CrdtPrdtType {
    일반신용대출("1", "일반신용대출"),
    마이너스한도대출("2", "마이너스한도대출"),
    장기카드대출("3","장기카드대출(카드론)");

    private final String code;
    private final String name;

    public static String findNamefromCode(String code) {
        for (CrdtPrdtType it : CrdtPrdtType.values()) {
            if (it.getCode().equals(code)) {
                return it.getName();
            }
        }
        throw new IllegalArgumentException("No matching CrdtPrdtType for code: " + code);
    }
}
