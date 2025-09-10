package com.konkuk.moneymate.activities.util;

import com.google.gson.annotations.SerializedName;
import com.konkuk.moneymate.activities.entity.financialProduct.DepositProduct;
import com.konkuk.moneymate.activities.entity.financialProduct.DepositProductOption;
import com.konkuk.moneymate.activities.entity.financialProduct.FinancialCompany;
import com.konkuk.moneymate.activities.entity.financialProduct.FinancialCompanyRegion;
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
    private String interestRateType;

    @SerializedName("intr_rate_type_nm")
    private String interestRateTypeName;

    @SerializedName("save_trm")
    private String saveTerm;

    @SerializedName("intr_rate")
    private double interestRate;

    @SerializedName("intr_rate2")
    private double interestRate2;

    public DepositProductOption toEntity(DepositProduct depositProduct) {
        return new DepositProductOption(
                null,
                depositProduct,
                this.interestRateType.charAt(0),
                this.interestRateTypeName,
                this.saveTerm.isEmpty() ? null : Integer.parseInt(this.saveTerm),
                BigDecimal.valueOf(this.interestRate),
                BigDecimal.valueOf(this.interestRate2)
        );
    }
}
