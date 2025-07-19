package com.konkuk.moneymate.auth.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthRequest {
    private String userId;
    private String password;
    private String phoneNumber;

    private String userVerifyCode;
    // private String pwVerifyCode;
}
