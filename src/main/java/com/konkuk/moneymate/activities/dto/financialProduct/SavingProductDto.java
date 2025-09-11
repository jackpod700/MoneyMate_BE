package com.konkuk.moneymate.activities.dto.financialProduct;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class SavingProductDto extends FinancialProductDto {
//    FinancialProductDto fields
//    private String bankName; //은행명
//    private String productName; //상품명
//    private String joinWay; //가입방법
//    private LocalDate dclsStrtDay; //공시 시작일
//    private LocalDate dclsEndDay; //공시 종료일
//    private String url; //은행 URL
//    private String callNum; //은행 전화번호


    private String intrRate; // 저축 금리
    private String maxIntrRate; // 최고 우대 금리
    private String rsrvtype; // 적립식 구분
    private String intrType; // 이자계산방식
    private String mtrtInt; // 만기 후 이자율
    private String spclCnd; // 우대조건
    private String joinDeny; // 가입제한
    private String joinMember; // 가입대상
    private String etcNote; // 기타 유의사항
    private String maxLimit; // 최고한도

    public SavingProductDto(
            String bankName,
            String productName,
            String joinWay,
            LocalDate dclsStrtDay,
            LocalDate dclsEndDay,
            String url,
            String callNum,
            BigDecimal intrRate,
            BigDecimal maxIntrRate,
            String rsrvtype,
            String intrType,
            String mtrtInt,
            String spclCnd,
            String joinDeny,
            String joinMember,
            String etcNote,
            Long maxLimit
    ) {
        super(bankName, productName, joinWay, dclsStrtDay, dclsEndDay, url, callNum);
        this.intrRate = intrRate.toString();
        this.maxIntrRate = maxIntrRate.toString();
        this.rsrvtype = rsrvtype;
        this.intrType = intrType;
        this.mtrtInt = mtrtInt;
        this.spclCnd = spclCnd;
        this.joinDeny = joinDeny;
        this.joinMember = joinMember;
        this.etcNote = etcNote;
        this.maxLimit = maxLimit!=null?maxLimit.toString():null;
    }

}
