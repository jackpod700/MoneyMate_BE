package com.konkuk.moneymate.activities.dto.financialProduct;

public class CreditLoanProductDto extends FinancialProductDto{
    // FinancialProductDto fields
    // private String bankName; //은행명
    // private String productName; //상품명
    // private String joinWay; //가입방법
    // private LocalDate dclsStrtDay; //공시 시작일
    // private LocalDate dclsEndDay; //공시 종료일
    // private String url; //은행 URL
    // private String callNum; //은행 전화번호

    private String crdtPrdtType; // 대출 금리 유형
    private String crdtLendRateType;
    private String crdtGrad;
    private String cbName;

    public CreditLoanProductDto(
            String bankName,
            String productName,
            String joinWay,
            java.time.LocalDate dclsStrtDay,
            java.time.LocalDate dclsEndDay,
            String url,
            String callNum,
            String crdtPrdtType,
            String crdtLendRateType,
            String crdtGrad,
            String cbName
    ) {
        super(bankName, productName, joinWay, dclsStrtDay, dclsEndDay, url, callNum);
        this.crdtPrdtType = crdtPrdtType;
        this.crdtLendRateType = crdtLendRateType;
        this.crdtGrad = crdtGrad;
        this.cbName = cbName;
    }
}
