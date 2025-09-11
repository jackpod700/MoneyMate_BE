package com.konkuk.moneymate.activities.dto.financialProduct;

import lombok.Getter;

@Getter
public class MortgageLoanProductDto extends FinancialProductDto {
    // FinancialProductDto fields
    // private String bankName; //은행명
    // private String productName; //상품명
    // private String joinWay; //가입방법
    // private LocalDate dclsStrtDay; //공시 시작일
    // private LocalDate dclsEndDay; //공시 종료일
    // private String url; //은행 URL
    // private String callNum; //은행 전화번호

    private String lendRateType; // 대출 금리 유형
    private String lendRateMin;
    private String lendRateMax;
    private String lendRateAvg;
    private String loanInciExpn;
    private String erlyRpayFee;
    private String dlyRate;
    private String loanLmt;
    private String mrtgType;
    private String rpayType;

    public MortgageLoanProductDto(
            String bankName,
            String productName,
            String joinWay,
            java.time.LocalDate dclsStrtDay,
            java.time.LocalDate dclsEndDay,
            String url,
            String callNum,
            String lendRateType,
            String lendRateMin,
            String lendRateMax,
            String lendRateAvg,
            String loanInciExpn,
            String erlyRpayFee,
            String dlyRate,
            String loanLmt,
            String mrtgType,
            String rpayType
    ) {
        super(bankName, productName, joinWay, dclsStrtDay, dclsEndDay, url, callNum);
        this.lendRateType = lendRateType;
        this.lendRateMin = lendRateMin;
        this.lendRateMax = lendRateMax;
        this.lendRateAvg = lendRateAvg;
        this.loanInciExpn = loanInciExpn;
        this.erlyRpayFee = erlyRpayFee;
        this.dlyRate = dlyRate;
        this.loanLmt = loanLmt;
        this.mrtgType = mrtgType;
        this.rpayType = rpayType;
    }
}
