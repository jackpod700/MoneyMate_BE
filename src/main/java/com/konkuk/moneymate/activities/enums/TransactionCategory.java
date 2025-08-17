package com.konkuk.moneymate.activities.enums;

public enum TransactionCategory {


    /**
     * 수입
     */
    SALARY("근로소득"),
    REFUND("환불"),
    INTEREST("이자수입"),
    DIVIDEND("배당수입"),
    ALLOWANCE("용돈"),
    TAX_REFUND("세금-환급"), // 주로 연말정산에서
    LOTTERY("복권/당첨금"),


    /**
     * 지출
     */
    UTILITIES("공과금"), // 세금은 따로
    TAX("세금"),
    LOAN_REPAYMENT("대출상환"),
    TRANSPORT("교통비"),
    SHOPPING("쇼핑"),
    CONVENIENCE("생활"),
    MART("마트"),
    COMMUNICATION("통신비"),
    BOOKS("서적"),
    DINING("음식점"),
    DELIVERY("배달음식"),
    CONVENIENCE_STORE("편의점"),
    HOUSING("주거"),  // 월세, 관리비
    HEALTHCARE("의료"), // 병원, 약국
    EDUCATION("교육"),  // 학원, 강의
    TUITION("학비"),
    ENTERTAINMENT("문화생활"), // 영화, 뮤지컬, 전시, 박물관
    SUBSCRIPTION("구독서비스"), // 정기 구독 서비스
    ACCOMMODATION("숙박"),
    PET("반려동물"),
    CAR("자동차"),
    CAFE("카페"),
    BEAUTY("미용/뷰티"),
    CLOTHING("의류/패션"),
    INSURANCE("보험"),
    DONATION("기부"),  // 후원
    PAYMENT("간편결제"),
    GIFT("선물"),
    ONLINE_MALL("온라인쇼핑"),

    /**
     * 은행
     */
    TRANSFER("이체"), // 출금 전용
    INVESTMENT("투자"),    // 투자상품에
    DEPOSIT("입금"), // 입금 전용
    WITHDRAWAL("출금"), // 이체 말고 현금 인출하는것
    SAVINGS("예적금"),
    BUSINESS("사업"),
    CARD_PAYMENT("카드대금"),   // 월별 카드대금
    EXCHANGE("환전"),              // 외화 거래
    REMITTANCE("해외송금"),         // 외화 보내기
    STOCK("주식거래"),




    CORRECTION("거래정정"),
    ETC("기타");


    private final String categoryName;

    TransactionCategory(String displayName) {
        this.categoryName = displayName;
    }

    public String getDisplayName() {
        return categoryName;
    }

}
