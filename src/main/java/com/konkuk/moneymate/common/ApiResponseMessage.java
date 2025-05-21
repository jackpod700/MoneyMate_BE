package com.konkuk.moneymate.common;

public enum ApiResponseMessage {
    ACCOUNT_REGISTER_SUCCESS("계좌등록 성공");

    private String message;

    ApiResponseMessage(String s) {
        this.message = s;
    }
    public String getMessage() {return message;}
}
