package com.konkuk.moneymate.activities.financial.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class DepositProductDto extends FinancialProductDto{
    private String intrRate; // 저축 금리
    private String maxIntrRate; // 최고 우대 금리
    private String intrType; // 이자계산방식
    private String mtrtInt; // 만기 후 이자율
    private String spclCnd; // 우대조건
    private String joinDeny; // 가입제한
    private String joinMember; // 가입대상
    private String etcNote; // 기타 유의사항
    private String maxLimit; // 최고한도

    public DepositProductDto(String bankName,
                             String productName,
                             String joinWay,
                             LocalDate dclsStrtDay,
                             LocalDate dclsEndDay,
                             String url,
                             String callNum,
                             BigDecimal intrRate,
                             BigDecimal maxIntrRate,
                             String intrType,
                             String mtrtInt,
                             String spclCnd,
                             String joinDeny,
                             String joinMember,
                             String etcNote,
                             Long maxLimit) {
        super(bankName, productName, joinWay, dclsStrtDay, dclsEndDay, url, callNum);
        this.intrRate = intrRate.toString();
        this.maxIntrRate = maxIntrRate.toString();
        this.intrType = intrType;
        this.mtrtInt = mtrtInt;
        this.spclCnd = spclCnd;
        this.joinDeny = joinDeny;
        this.joinMember = joinMember;
        this.etcNote = etcNote;
        this.maxLimit = maxLimit!=null?maxLimit.toString():null;
    }
}
