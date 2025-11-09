package com.konkuk.moneymate.activities.financial.util;

import com.konkuk.moneymate.activities.financial.entity.DepositProduct;
import com.konkuk.moneymate.activities.financial.entity.FinancialCompany;
import com.konkuk.moneymate.activities.financial.entity.MortgageLoanProduct;
import com.konkuk.moneymate.activities.financial.entity.RentHouseLoanProduct;
import com.konkuk.moneymate.activities.financial.entity.SavingProduct;
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
            DepositProduct.class),
    SAVING_PRODUCT(
            "savingProductsSearch.json",
            "적금 상품 조회 중 오류 발생",
            SavingProductBaseInfo.class,
            SavingProductOptionInfo.class,
            SavingProduct.class),
    MORTGAGE_LOAN_PRODUCT(
            "mortgageLoanProductsSearch.json",
            "주택담보대출 상품 조회 중 오류 발생",
            MortgageLoanProductBaseInfo.class,
            MortgageLoanProductOptionInfo.class,
            MortgageLoanProduct.class),
    RENT_HOUSE_LOAN_PRODUCT(
            "rentHouseLoanProductsSearch.json",
            "전세자금대출 상품 조회 중 오류 발생",
            RentHouseLoanProductBaseInfo.class,
            RentHouseLoanProductOptionInfo.class,
            RentHouseLoanProduct.class),
    CREDIT_LOAN_PRODUCT(
            "creditLoanProductsSearch.json",
            "신용대출 상품 조회 중 오류 발생",
            CreditLoanProductBaseInfo.class,
            CreditLoanProductOptionInfo.class,
            MortgageLoanProduct.class);
    ;



    private String apiName;
    private String exceptionMessage;
    private Class<?> baseInfo;
    private Class<?> optionInfo;
    private Class<?> entity;
}
