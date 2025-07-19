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
    private String message;
    private String userVerifyCode;


    public static SmsMessageResponse of(String phoneNumber, Integer verifyCode, String message, String userVerifyCode) {
        return new SmsMessageResponse(phoneNumber, verifyCode, message, userVerifyCode);
    }
}
