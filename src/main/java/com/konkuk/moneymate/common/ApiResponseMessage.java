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
    NEWS_TOTAL_SUCCESS("전체뉴스조회 성공"),
    NEWS_DETAIL_SUCCESS("뉴스상세조회 성공"),
    //실패시
    USER_NOT_FOUND("사용자를 찾을 수 없습니다"),
    ACCOUNT_NOT_FOUND("계좌를 찾을 수 없습니다"),
    ASSET_NOT_FOUND("자산을 찾을 수 없습니다"),
    PUBLISHER_OR_CATEGORY_NOT_FOUND("뉴스사 또는 카테고리를 찾을 수 없습니다"),
    WRONG_FORMAT("잘못된 형식"),
    NO_ACCESS_AUTHORITY("접근권한 없음"),
    INTERNAL_SERVER_ERROR("서버 내부 오류"),

    USER_LOGIN_SUCCESS("로그인 성공"),
    USER_LOGIN_FAIL("로그인 실패, 아이디와 비밀번호를 확인해 주세요"),
    USER_LOGOUT_SUCCESS("로그아웃 성공"),
    USER_DELETE_SUCCESS("회원탈퇴 성공"),
    USER_ID_PW_VERIFY_SUCCESS("ID-PW 확인 성공"),
    USER_ID_PW_VERIFY_FAIL("ID-PW 확인 실패"),

    INVALID_TOKEN("This token is Invalid"),
    INVALID_REFRESH_TOKEN("This refresh token is Invalid"),
    INVALID_ACCESS_TOKEN("This access token is Invalid"),
    TOKEN_REISSUE_SUCCESS("토큰 재발급 성공"),

    USER_REGISTER_SUCCESS("회원가입 성공"),
    USER_REGISTER_FAIL("회원가입 실패"),
    USER_ID_EXISTS("이미 존재하는 ID 입니다"),
    USER_ID_NOT_EXISTS("존재하지 않는 ID 입니다"),
    USER_ID_AVAILABLE("사용 가능한 ID 입니다"),
    USER_ID_FIND_SUCCESS("아이디 찾기 성공"),

    SMS_SEND_SUCCESS("인증 요청을 성공적으로 보냈습니다"),
    SMS_VERIFY_SUCCESS("sms 인증이 완료되었습니다"),
    SMS_VERIFY_FAIL("sms 인증 실패, 다시 시도해 주세요"),

    BAD_REQUEST("[400] 기타 오류가 발생했습니다");



    private String message;

    ApiResponseMessage(String s) {
        this.message = s;
    }
    public String getMessage() {return message;}
}
