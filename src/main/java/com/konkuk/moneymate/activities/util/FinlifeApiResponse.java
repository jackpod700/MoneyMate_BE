package com.konkuk.moneymate.activities.util;

import com.google.gson.annotations.SerializedName;
import com.konkuk.moneymate.activities.entity.financialProduct.CreditLoanProduct;
import com.konkuk.moneymate.activities.entity.financialProduct.CreditLoanProductOption;
import com.konkuk.moneymate.activities.entity.financialProduct.DepositProduct;
import com.konkuk.moneymate.activities.entity.financialProduct.DepositProductOption;
import com.konkuk.moneymate.activities.entity.financialProduct.FinancialCompany;
import com.konkuk.moneymate.activities.entity.financialProduct.FinancialCompanyRegion;
import com.konkuk.moneymate.activities.entity.financialProduct.MortgageLoanProduct;
import com.konkuk.moneymate.activities.entity.financialProduct.MortgageLoanProductOption;
import com.konkuk.moneymate.activities.entity.financialProduct.RentHouseLoanProduct;
import com.konkuk.moneymate.activities.entity.financialProduct.RentHouseLoanProductOption;
import com.konkuk.moneymate.activities.entity.financialProduct.SavingProduct;
import com.konkuk.moneymate.activities.entity.financialProduct.SavingProductOption;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * API 응답의 최상위 구조를 나타내는 제네릭 클래스
 * @param <B> baseList의 요소 타입
 * @param <O> optionList의 요소 타입
 */
@Getter
@Setter
public class FinlifeApiResponse<B, O> {

    @SerializedName("result")
    private ApiResult<B, O> result;
}

/**
 * 'result' 객체의 내용을 나타내는 제네릭 클래스
 * @param <B> baseList의 요소 타입
 * @param <O> optionList의 요소 타입
 */
@Getter
@Setter
class ApiResult<B, O> {

    @SerializedName("prdt_div")
    private String productDivision;

    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("max_page_no")
    private int maxPageNo;

    @SerializedName("now_page_no")
    private int nowPageNo;

    @SerializedName("err_cd")
    private String errorCode;

    @SerializedName("err_msg")
    private String errorMessage;

    // 이 부분이 제네릭으로 처리되어 어떤 타입의 리스트든 담을 수 있습니다.
    @SerializedName("baseList")
    private List<B> baseList;

    // 이 부분도 마찬가지입니다.
    @SerializedName("optionList")
    private List<O> optionList;
}

@Getter
@Setter
class FinancialCompanyBaseInfo {

    @SerializedName("dcls_month")
    private String disclosureMonth;

    @SerializedName("fin_co_no")
    private String financialCompanyNo;

    @SerializedName("kor_co_nm")
    private String koreanCompanyName;

    @SerializedName("dcls_chrg_man")
    private String disclosureManager;

    @SerializedName("homp_url")
    private String homepageUrl;

    @SerializedName("cal_tel")
    private String contactNumber;

    public FinancialCompany toEntity(String finGroupCode) {
        return new FinancialCompany(
                this.financialCompanyNo,
                this.koreanCompanyName,
                finGroupCode,
                this.homepageUrl,
                this.contactNumber
        );
    }
}

@Getter
@Setter
class FinancialCompanyOptionInfo {

    @SerializedName("dcls_month")
    private String disclosureMonth;

    @SerializedName("fin_co_no")
    private String financialCompanyNo;

    @SerializedName("area_cd")
    private String areaCode;

    @SerializedName("area_nm")
    private String areaName;

    @SerializedName("exis_yn")
    private String existsYn;

    public FinancialCompanyRegion toEntity(FinancialCompany financialCompany) {
        return new FinancialCompanyRegion(
                financialCompany,
                this.areaCode,
                this.areaName
        );
    }
}

@Getter
@Setter
class DepositProductBaseInfo {

    @SerializedName("dcls_month")
    private String disclosureMonth;

    @SerializedName("fin_co_no")
    private String financialCompanyNo;

    @SerializedName("fin_prdt_cd")
    private String financialProductCode;

    @SerializedName("kor_co_nm")
    private String koreanCompanyName;

    @SerializedName("fin_prdt_nm")
    private String financialProductName;

    @SerializedName("join_way")
    private String joinWay;

    @SerializedName("mtrt_int")
    private String maturityInterest;

    @SerializedName("spcl_cnd")
    private String specialCondition;

    @SerializedName("join_deny")
    private String joinDeny;

    @SerializedName("join_member")
    private String joinMember;

    @SerializedName("etc_note")
    private String etcNote;

    @SerializedName("max_limit")
    private Long maxLimit;

    @SerializedName("dcls_strt_day")
    private String disclosureStartDate;

    @SerializedName("dcls_end_day")
    private String disclosureEndDate;

    @SerializedName("fin_co_subm_day")
    private String financialCompanySubmitDay;

    public DepositProduct toEntity(FinancialCompany financialCompany) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return new DepositProduct(
                null,
                financialCompany,
                this.financialProductCode,
                this.financialProductName,
                this.joinWay,
                this.disclosureStartDate!=null ? LocalDate.parse(this.disclosureStartDate, formatter) : null,
                this.disclosureEndDate!=null ? LocalDate.parse(this.disclosureEndDate, formatter) : null,
                this.maturityInterest,
                this.specialCondition,
                this.joinDeny,
                this.joinMember,
                this.etcNote,
                this.maxLimit
        );
    }
}

@Getter
@Setter
class DepositProductOptionInfo {

    @SerializedName("dcls_month")
    private String disclosureMonth;

    @SerializedName("fin_co_no")
    private String financialCompanyNo;

    @SerializedName("fin_prdt_cd")
    private String financialProductCode;

    @SerializedName("intr_rate_type")
    private char interestRateType;

    @SerializedName("intr_rate_type_nm")
    private String interestRateTypeName;

    @SerializedName("save_trm")
    private String saveTerm;

    @SerializedName("intr_rate")
    private BigDecimal interestRate;

    @SerializedName("intr_rate2")
    private BigDecimal interestRate2;

    public DepositProductOption toEntity(DepositProduct depositProduct) {
        return new DepositProductOption(
                null,
                depositProduct,
                this.interestRateType,
                this.interestRateTypeName,
                this.saveTerm.isEmpty() ? null : Integer.parseInt(this.saveTerm),
                this.interestRate,
                this.interestRate2
        );
    }
}

@Getter
@Setter
class SavingProductBaseInfo {

    @SerializedName("dcls_month")
    private String disclosureMonth;

    @SerializedName("fin_co_no")
    private String financialCompanyNo;

    @SerializedName("fin_prdt_cd")
    private String financialProductCode;

    @SerializedName("kor_co_nm")
    private String koreanCompanyName;

    @SerializedName("fin_prdt_nm")
    private String financialProductName;

    @SerializedName("join_way")
    private String joinWay;

    @SerializedName("mtrt_int")
    private String maturityInterest;

    @SerializedName("spcl_cnd")
    private String specialCondition;

    @SerializedName("join_deny")
    private String joinDeny;

    @SerializedName("join_member")
    private String joinMember;

    @SerializedName("etc_note")
    private String etcNote;

    @SerializedName("max_limit")
    private Long maxLimit;

    @SerializedName("dcls_strt_day")
    private String disclosureStartDate;

    @SerializedName("dcls_end_day")
    private String disclosureEndDate;

    @SerializedName("fin_co_subm_day")
    private String financialCompanySubmitDay;

    public SavingProduct toEntity(FinancialCompany financialCompany) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return new SavingProduct(
                null,
                financialCompany,
                this.financialProductCode,
                this.financialProductName,
                this.joinWay,
                this.disclosureStartDate!=null ? LocalDate.parse(this.disclosureStartDate, formatter) : null,
                this.disclosureEndDate!=null ? LocalDate.parse(this.disclosureEndDate, formatter) : null,
                this.maturityInterest,
                this.specialCondition,
                this.joinDeny,
                this.joinMember,
                this.etcNote,
                this.maxLimit
        );
    }
}

@Getter
@Setter
class SavingProductOptionInfo {

    @SerializedName("dcls_month")
    private String disclosureMonth;

    @SerializedName("fin_co_no")
    private String financialCompanyNo;

    @SerializedName("fin_prdt_cd")
    private String financialProductCode;

    @SerializedName("intr_rate_type")
    private char interestRateType;

    @SerializedName("intr_rate_type_nm")
    private String interestRateTypeName;

    @SerializedName("rsrv_type")
    private char rsrvType;

    @SerializedName("rsrv_type_nm")
    private String rsrvTypeName;

    @SerializedName("save_trm")
    private String saveTerm;

    @SerializedName("intr_rate")
    private BigDecimal interestRate;

    @SerializedName("intr_rate2")
    private BigDecimal interestRate2;

    public SavingProductOption toEntity(SavingProduct savingProduct) {
        return new SavingProductOption(
                null,
                savingProduct,
                this.rsrvType,
                this.rsrvTypeName,
                this.interestRateType,
                this.interestRateTypeName,
                this.saveTerm.isEmpty() ? null : Integer.parseInt(this.saveTerm),
                this.interestRate,
                this.interestRate2
        );
    }
}

@Getter
@Setter
class MortgageLoanProductBaseInfo {

    @SerializedName("dcls_month")
    private String disclosureMonth;

    @SerializedName("fin_co_no")
    private String financialCompanyNo;

    @SerializedName("fin_prdt_cd")
    private String financialProductCode;

    @SerializedName("kor_co_nm")
    private String koreanCompanyName;

    @SerializedName("fin_prdt_nm")
    private String financialProductName;

    @SerializedName("join_way")
    private String joinWay;

    @SerializedName("loan_inci_expn")
    private String loanInciExpn;

    @SerializedName("erly_rpay_fee")
    private String earlyRepayFee;

    @SerializedName("dly_rate")
    private String delayRate;

    @SerializedName("loan_lmt")
    private String loanLimit;

    @SerializedName("dcls_strt_day")
    private String disclosureStartDate;

    @SerializedName("dcls_end_day")
    private String disclosureEndDate;

    @SerializedName("fin_co_subm_day")
    private String financialCompanySubmitDay;

    public MortgageLoanProduct toEntity(FinancialCompany financialCompany) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return new MortgageLoanProduct(
                null,
                financialCompany,
                this.financialProductCode,
                this.financialProductName,
                this.joinWay,
                this.loanInciExpn,
                this.earlyRepayFee,
                this.delayRate,
                this.loanLimit,
                this.disclosureStartDate!=null ? LocalDate.parse(this.disclosureStartDate, formatter) : null,
                this.disclosureEndDate!=null ? LocalDate.parse(this.disclosureEndDate, formatter) : null
        );
    }
}

@Getter
@Setter
class MortgageLoanProductOptionInfo {

    @SerializedName("dcls_month")
    private String disclosureMonth;

    @SerializedName("fin_co_no")
    private String financialCompanyNo;

    @SerializedName("fin_prdt_cd")
    private String financialProductCode;

    @SerializedName("mrtg_type")
    private char mortgageType;

    @SerializedName("mrtg_type_nm")
    private String mortgageTypeName;

    @SerializedName("rpay_type")
    private char repaymentType;

    @SerializedName("rpay_type_nm")
    private String repaymentTypeName;

    @SerializedName("lend_rate_type")
    private char lendRateType;

    @SerializedName("lend_rate_type_nm")
    private String lendRateTypeName;

    @SerializedName("lend_rate_min")
    private BigDecimal lendRateMin;

    @SerializedName("lend_rate_max")
    private BigDecimal lendRateMax;

    @SerializedName("lend_rate_avg")
    private BigDecimal lendRateAvg;

    public MortgageLoanProductOption toEntity(MortgageLoanProduct mortgageLoanProduct) {
        return new MortgageLoanProductOption(
                null,
                mortgageLoanProduct,
                this.mortgageType,
                this.mortgageTypeName,
                this.repaymentType,
                this.repaymentTypeName,
                this.lendRateType,
                this.lendRateTypeName,
                this.lendRateMin,
                this.lendRateMax,
                this.lendRateAvg
        );
    }
}

@Getter
@Setter
class RentHouseLoanProductBaseInfo {

    @SerializedName("dcls_month")
    private String disclosureMonth;

    @SerializedName("fin_co_no")
    private String financialCompanyNo;

    @SerializedName("fin_prdt_cd")
    private String financialProductCode;

    @SerializedName("kor_co_nm")
    private String koreanCompanyName;

    @SerializedName("fin_prdt_nm")
    private String financialProductName;

    @SerializedName("join_way")
    private String joinWay;

    @SerializedName("loan_inci_expn")
    private String loanInciExpn;

    @SerializedName("erly_rpay_fee")
    private String earlyRepayFee;

    @SerializedName("dly_rate")
    private String delayRate;

    @SerializedName("loan_lmt")
    private String loanLimit;

    @SerializedName("dcls_strt_day")
    private String disclosureStartDate;

    @SerializedName("dcls_end_day")
    private String disclosureEndDate;

    @SerializedName("fin_co_subm_day")
    private String financialCompanySubmitDay;

    public RentHouseLoanProduct toEntity(FinancialCompany financialCompany) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return new RentHouseLoanProduct(
                null,
                financialCompany,
                this.financialProductCode,
                this.financialProductName,
                this.joinWay,
                this.loanInciExpn,
                this.earlyRepayFee,
                this.delayRate,
                this.loanLimit,
                this.disclosureStartDate!=null ? LocalDate.parse(this.disclosureStartDate, formatter) : null,
                this.disclosureEndDate!=null ? LocalDate.parse(this.disclosureEndDate, formatter) : null
        );
    }
}

@Getter
@Setter
class RentHouseLoanProductOptionInfo {

    @SerializedName("dcls_month")
    private String disclosureMonth;

    @SerializedName("fin_co_no")
    private String financialCompanyNo;

    @SerializedName("fin_prdt_cd")
    private String financialProductCode;

    @SerializedName("rpay_type")
    private char repaymentType;

    @SerializedName("rpay_type_nm")
    private String repaymentTypeName;

    @SerializedName("lend_rate_type")
    private char lendRateType;

    @SerializedName("lend_rate_type_nm")
    private String lendRateTypeName;

    @SerializedName("lend_rate_min")
    private BigDecimal lendRateMin;

    @SerializedName("lend_rate_max")
    private BigDecimal lendRateMax;

    @SerializedName("lend_rate_avg")
    private BigDecimal lendRateAvg;

    public RentHouseLoanProductOption toEntity(RentHouseLoanProduct rentHouseLoanProduct) {
        return new RentHouseLoanProductOption(
                null,
                rentHouseLoanProduct,
                this.repaymentType,
                this.repaymentTypeName,
                this.lendRateType,
                this.lendRateTypeName,
                this.lendRateMin,
                this.lendRateMax,
                this.lendRateAvg
        );
    }
}

@Getter
@Setter
class CreditLoanProductBaseInfo {

    @SerializedName("dcls_month")
    private String disclosureMonth;

    @SerializedName("fin_co_no")
    private String financialCompanyNo;

    @SerializedName("kor_co_nm")
    private String koreanCompanyName;

    @SerializedName("fin_prdt_cd")
    private String financialProductCode;

    @SerializedName("fin_prdt_nm")
    private String financialProductName;

    @SerializedName("join_way")
    private String joinWay;

    @SerializedName("crdt_prdt_type")
    private char creditProductType;

    @SerializedName("crdt_prdt_type_nm")
    private String creditProductTypeName;

    @SerializedName("cb_name")
    private String cbName;

    @SerializedName("dcls_strt_day")
    private String disclosureStartDate;

    @SerializedName("dcls_end_day")
    private String disclosureEndDate;

    @SerializedName("fin_co_subm_day")
    private String financialCompanySubmitDay;

    /**
     * 이 DTO를 CreditLoanProduct 엔티티로 변환합니다.
     * @param financialCompany FinancialCompany 엔티티
     * @return CreditLoanProduct 엔티티
     */
    public CreditLoanProduct toEntity(FinancialCompany financialCompany) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return new CreditLoanProduct(
                null,
                financialCompany,
                this.financialProductCode,
                this.financialProductName,
                this.joinWay,
                this.creditProductType,
                this.creditProductTypeName,
                this.cbName,
                (this.disclosureStartDate != null && !this.disclosureStartDate.isEmpty()) ? LocalDate.parse(this.disclosureStartDate, formatter) : null,
                (this.disclosureEndDate != null && !this.disclosureEndDate.isEmpty()) ? LocalDate.parse(this.disclosureEndDate, formatter) : null
        );
    }
}

@Getter
@Setter
class CreditLoanProductOptionInfo {

    @SerializedName("dcls_month")
    private String disclosureMonth;

    @SerializedName("fin_co_no")
    private String financialCompanyNo;

    @SerializedName("fin_prdt_cd")
    private String financialProductCode;

    @SerializedName("crdt_prdt_type")
    private String creditProductType;

    @SerializedName("crdt_lend_rate_type")
    private char creditLendRateType;

    @SerializedName("crdt_lend_rate_type_nm")
    private String creditLendRateTypeName;

    @SerializedName("crdt_grad_1")
    private BigDecimal creditGrade1;

    @SerializedName("crdt_grad_4")
    private BigDecimal creditGrade4;

    @SerializedName("crdt_grad_5")
    private BigDecimal creditGrade5;

    @SerializedName("crdt_grad_6")
    private BigDecimal creditGrade6;

    @SerializedName("crdt_grad_10")
    private BigDecimal creditGrade10;

    @SerializedName("crdt_grad_11")
    private BigDecimal creditGrade11;

    @SerializedName("crdt_grad_12")
    private BigDecimal creditGrade12;

    @SerializedName("crdt_grad_13")
    private BigDecimal creditGrade13;

    @SerializedName("crdt_grad_avg")
    private BigDecimal creditGradeAvg;

    /**
     * 이 DTO를 CreditLoanProductOption 엔티티로 변환합니다.
     * @param creditLoanProduct 부모가 될 CreditLoanProduct 엔티티
     * @return CreditLoanProductOption 엔티티
     */
    public CreditLoanProductOption toEntity(CreditLoanProduct creditLoanProduct) {
        return new CreditLoanProductOption(
                null,
                creditLoanProduct,
                this.creditLendRateType,
                this.creditLendRateTypeName,
                this.creditGrade1,
                this.creditGrade4,
                this.creditGrade5,
                this.creditGrade6,
                this.creditGrade10,
                this.creditGrade11,
                this.creditGrade12,
                this.creditGrade13,
                this.creditGradeAvg
        );
    }
}