package com.konkuk.moneymate.auth.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmsMessageResponse<T> {
    private String phoneNumber;
    private Integer verifyCode;

    // 사용 안 함
    private String message;

    public static SmsMessageResponse of(String phoneNumber, Integer verifyCode, String message) {
        return new SmsMessageResponse(phoneNumber, verifyCode, message);
    }
}
