package com.konkuk.moneymate.activities.dto.financial.product;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class FinancialProductDto {
    private String bankName; //은행명
    private String productName; //상품명
    private String joinWay; //가입방법
    private LocalDate dclsStrtDay; //공시 시작일
    private LocalDate dclsEndDay; //공시 종료일
    private String url; //은행 URL
    private String callNum; //은행 전화번호
}
