package com.konkuk.moneymate.activities.dto.financialProduct;

import java.time.LocalDate;

public class SavingProductDto extends FinancialProductDto {
    private String maturityInterest; // 만기 후 이자율
    private String specialCondition; // 우대조건
    private String joinDeny; // 가입제한
    private String joinMember; // 가입대상
    private String etcNote; // 기타 유의사항
    private String maxLimit; // 최고한도

    public SavingProductDto(String bankName,
                            String productName,
                            String joinWay,
                            LocalDate dclsStrtDay,
                            LocalDate dclsEndDay,
                            String url,
                            String callNum,
                            String maturityInterest,
                            String specialCondition,
                            String joinDeny,
                            String joinMember,
                            String etcNote,
                            Long maxLimit) {
        super(bankName, productName, joinWay, dclsStrtDay, dclsEndDay, url, callNum);
        this.maturityInterest = maturityInterest;
        this.specialCondition = specialCondition;
        this.joinDeny = joinDeny;
        this.joinMember = joinMember;
        this.etcNote = etcNote;
        this.maxLimit = maxLimit!=null?maxLimit.toString():null;
    }
}
