package com.konkuk.moneymate.common;

public enum ApiResponseMessage {
    //성공시
    ACCOUNT_REGISTER_SUCCESS("계좌등록 성공"),
    ACCOUNT_QUERY_SUCCESS("계좌조회 성공"),
    TRANSACTION_QUERY_SUCCESS("거래내역조회 성공"),
    ACCOUNT_DELETE_SUCCESS("계좌삭제 성공"),
    ASSET_REGISTER_SUCCESS("자산등록 성공"),
    ASSET_QUERY_SUCCESS("자산조회 성공"),
    ASSET_DELETE_SUCCESS("자산삭제 성공"),
    ASSET_TOTAL_SUCCESS("전체자산조회 성공"),
    //실패시
    USER_NOT_FOUND("사용자를 찾을 수 없습니다"),
    ACCOUNT_NOT_FOUND("계좌를 찾을 수 없습니다"),
    ASSET_NOT_FOUND("자산을 찾을 수 없습니다"),
    WRONG_FORMAT("잘못된 형식"),
    NO_ACCESS_AUTHORITY("접근권한 없음"),
    INTERNAL_SERVER_ERROR("서버 내부 오류");
    private String message;

    ApiResponseMessage(String s) {
        this.message = s;
    }
    public String getMessage() {return message;}
}
