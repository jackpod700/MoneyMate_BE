package com.konkuk.moneymate.activities.util;

import com.konkuk.moneymate.activities.entity.financialProduct.DepositProduct;
import com.konkuk.moneymate.activities.entity.financialProduct.FinancialCompany;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FinlifeFunctionParam {
    FINANCIAL_COMPANY(
            "companySearch.json",
            "금융회사 조회 중 오류 발생",
            FinancialCompanyBaseInfo.class,
            FinancialCompanyOptionInfo.class,
            FinancialCompany.class),
    DEPOSIT_PRODUCT(
            "depositProductsSearch.json",
            "정기예금 상품 조회 중 오류 발생",
            DepositProductBaseInfo.class,
            DepositProductOptionInfo.class,
            DepositProduct.class);

    private String apiName;
    private String exceptionMessage;
    private Class<?> baseInfo;
    private Class<?> optionInfo;
    private Class<?> entity;
}
