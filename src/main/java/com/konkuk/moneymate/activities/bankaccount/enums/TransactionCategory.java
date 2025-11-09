package com.konkuk.moneymate.activities.bankaccount.enums;

import java.util.Arrays;

public enum TransactionCategory {

    /**
     * 수입
     */
    SALARY(FlowType.INCOME, "근로소득"),
    INVESTMENT_INCOME(FlowType.INCOME, "투자수입"),
    ALLOWANCE(FlowType.INCOME, "용돈"),
    LOTTERY(FlowType.INCOME, "복권/당첨금"),
    ETC_INCOME(FlowType.INCOME, "기타수입"),

    /**
     * 지출
     */
    HOUSING_AND_TAX(FlowType.OUTCOME, "주거/공과금"),
    TRANSPORT(FlowType.OUTCOME, "교통/자동차"),
    FOOD(FlowType.OUTCOME, "식비"),
    CAFE(FlowType.OUTCOME, "카페"),
    SHOPPING(FlowType.OUTCOME, "생활/쇼핑"),
    CONVENIENCE_STORE(FlowType.OUTCOME, "편의점"),
    HEALTHCARE(FlowType.OUTCOME, "의료/건강"),
    ENTERTAINMENT(FlowType.OUTCOME, "문화생활/취미"),
    TRIP(FlowType.OUTCOME, "여행/숙박"),
    EDUCATION(FlowType.OUTCOME, "교육"),
    SUBSCRIPTION(FlowType.OUTCOME, "정기결제"),
    BOOKS(FlowType.OUTCOME, "서적"),
    PAYMENT(FlowType.OUTCOME, "간편결제"),
    ETC_OUTCOME(FlowType.OUTCOME, "기타지출"),

    /**
     * 은행
     */
    TRANSFER(FlowType.ETC, "이체"),
    DEPOSIT(FlowType.ETC, "입금"),
    WITHDRAWAL(FlowType.ETC, "출금"),
    SAVINGS(FlowType.ETC, "예적금"),
    EXCHANGE(FlowType.ETC, "환전"),
    REFUND(FlowType.INCOME, "환불"),
    // BUSINESS(FlowType.BOTH, "사업"),
    STOCK(FlowType.ETC, "주식거래"),
    ETC_BANK(FlowType.ETC, "기타");

    private final FlowType flow;
    private final String displayName;

    TransactionCategory(FlowType flow, String displayName) {
        this.flow = flow;
        this.displayName = displayName;
    }

    public FlowType getFlow() {
        return flow;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TransactionCategory fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(c -> c.displayName.equals(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown category: " + displayName));
    }

    public enum FlowType {
        INCOME, OUTCOME, BOTH, ETC // ETC : 소비내역에서 제외
    }
}
