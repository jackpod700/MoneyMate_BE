package com.konkuk.moneymate.activities.enums.financialProduct;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JoinWay {
    branch("영업점"),
    internet("인터넷"),
    smartphone("스마트폰"),
    recruiter("모집인"),
    telephone("전화(텔레뱅킹)"),
    others("기타");

    private final String description;

    public static String findByName(String name) {
        for (JoinWay jw : JoinWay.values()) {
            if (jw.name().equals(name)) {
                return jw.getDescription();
            }
        }
        throw new IllegalArgumentException("No matching JoinWay for name: " + name);
    }
}
