package com.konkuk.moneymate.activities.dto.financial.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

@Getter
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
    private String crdtGrad9;
    private String crdtGrad98;
    private String crdtGrad87;
    private String crdtGrad76;
    private String crdtGrad65;
    private String crdtGrad54;
    private String crdtGrad43;
    private String crdtGrad3;
    private String cbName;

    public CreditLoanProductDto(
            String bankName,
            String productName,
            String joinWay,
            LocalDate dclsStrtDay,
            LocalDate dclsEndDay,
            String url,
            String callNum,
            String crdtPrdtType,
            String crdtLendRateType,
            BigDecimal crdtGrad9,
            BigDecimal crdtGrad98,
            BigDecimal crdtGrad87,
            BigDecimal crdtGrad76,
            BigDecimal crdtGrad65,
            BigDecimal crdtGrad54,
            BigDecimal crdtGrad43,
            BigDecimal crdtGrad3,
            String cbName
    ) {
        super(bankName, productName, joinWay, dclsStrtDay, dclsEndDay, url, callNum);
        this.crdtPrdtType = crdtPrdtType;
        this.crdtLendRateType = crdtLendRateType;
        this.crdtGrad9=crdtGrad9!=null?crdtGrad9.toString():null;
        this.crdtGrad98=crdtGrad98!=null?crdtGrad98.toString():null;
        this.crdtGrad87=crdtGrad87!=null?crdtGrad87.toString():null;
        this.crdtGrad76=crdtGrad76!=null?crdtGrad76.toString():null;
        this.crdtGrad65=crdtGrad65!=null?crdtGrad65.toString():null;
        this.crdtGrad54=crdtGrad54!=null?crdtGrad54.toString():null;
        this.crdtGrad43=crdtGrad43!=null?crdtGrad43.toString():null;
        this.crdtGrad3=crdtGrad3!=null?crdtGrad3.toString():null;
        this.cbName = cbName;
    }
}
