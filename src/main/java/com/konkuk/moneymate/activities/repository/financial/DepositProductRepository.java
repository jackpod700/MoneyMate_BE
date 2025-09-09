package com.konkuk.moneymate.activities.repository.financial;

import com.konkuk.moneymate.activities.dto.financialProduct.DepositProductDto;
import com.konkuk.moneymate.activities.entity.financialProduct.DepositProduct;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositProductRepository extends JpaRepository<DepositProduct, UUID> {
    /*
    1. 금융권역코드, 지역 조건(리스트중 하나라도 가지고 있으면 됨)에 맞는 금융회사 뽑아내기
    2. 1번에서 뽑아낸 금융회사의 예적금 상품들 중에서
       - 가입제한
       - 가입방법
       - 최고한도
      조건에 맞는 상품들 뽑아내기
    3. 2번에서 뽑아낸 상품들의 금리옵션들 중에서
       - 가입기간
       - 이자계산방식
       조건에 맞는 금리옵션들 뽑아내기
   4. 위 조건들에 맞는 정기예금 상품들을 dto로 변환해서 리턴
    * */
    @Query(value="""
    SELECT 
""", nativeQuery=true)
    public List<DepositProductDto> findByMaxLimitandPeriodAndJoinDeny(
            @Param("maxLimit") Long maxLimit,
            @Param("period") Integer period,
            @Param("joinDeny") String joinDeny
    );

    @Query("""
    SELECT new com.konkuk.moneymate.activities.dto.financialProduct.DepositProductDto(
        dp.financialCompany.name,
        dp.productName,
        dp.joinWay,
        dp.disclosureStartDay,
        dp.disclosureEndDay,
        dp.financialCompany.homeUrl,
        dp.financialCompany.callNum,
        dpo.intrRate,
        dpo.intrRate2,
        dpo.intrRateTypeName,
        dp.maturityInterest,
        dp.specialCondition,
        dp.joinDeny,
        dp.joinMember,
        dp.etcNote,
        dp.maxLimit
    )
    FROM DepositProduct dp
    JOIN dp.options dpo
    WHERE dp.financialCompany.code IN :companyCodes
    AND (:savingAmount <= dp.maxLimit OR dp.maxLimit IS NULL)
    AND dpo.saveTerm = :saveTrm
    AND dp.joinDeny = :joinDeny
""")
    public List<DepositProductDto> findByFinancialCompanyCodeAndSavingAmountAndSaveTrmAndJoinDeny(
            @Param("companyCodes") List<String> companyCodes,
            @Param("savingAmount") int savingAmount,
            @Param("saveTrm") Integer saveTrm,
            @Param("joinDeny") String joinDeny);
}
