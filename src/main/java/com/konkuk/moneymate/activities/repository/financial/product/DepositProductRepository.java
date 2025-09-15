package com.konkuk.moneymate.activities.repository.financial.product;

import com.konkuk.moneymate.activities.dto.financial.product.DepositProductDto;
import com.konkuk.moneymate.activities.entity.financial.product.DepositProduct;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositProductRepository extends JpaRepository<DepositProduct, UUID> {

    @Query("""
    SELECT new com.konkuk.moneymate.activities.dto.financial.product.DepositProductDto(
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
    List<DepositProductDto> findByFinancialCompanyCodeAndSavingAmountAndSaveTrmAndJoinDeny(
            @Param("companyCodes") List<String> companyCodes,
            @Param("savingAmount") int savingAmount,
            @Param("saveTrm") Integer saveTrm,
            @Param("joinDeny") String joinDeny);
}
