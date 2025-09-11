package com.konkuk.moneymate.activities.repository.financial;

import com.konkuk.moneymate.activities.dto.financialProduct.SavingProductDto;
import com.konkuk.moneymate.activities.entity.financialProduct.SavingProduct;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingProductRepository extends JpaRepository<SavingProduct, UUID> {
    @Query("""
    SELECT new com.konkuk.moneymate.activities.dto.financialProduct.SavingProductDto(
        sp.financialCompany.name,
        sp.productName,
        sp.joinWay,
        sp.disclosureStartDay,
        sp.disclosureEndDay,
        sp.financialCompany.homeUrl,
        sp.financialCompany.callNum,
        spo.intrRate,
        spo.intrRate2,
        spo.rsrvTypeName,
        spo.intrRateTypeName,
        sp.maturityInterest,
        sp.specialCondition,
        sp.joinDeny,
        sp.joinMember,
        sp.etcNote,
        sp.maxLimit
    )
    FROM SavingProduct sp
    JOIN sp.options spo
    WHERE sp.financialCompany.code IN :companyCodes
    AND (:savingAmount <= sp.maxLimit OR sp.maxLimit IS NULL)
    AND spo.saveTerm = :saveTrm
    AND sp.joinDeny = :joinDeny
""")
    List<SavingProductDto> findByFinancialCompanyCodeAndSavingAmountAndSaveTrmAndJoinDeny(
            @Param("companyCodes") List<String> companyCodes,
            @Param("savingAmount") int savingAmount,
            @Param("saveTrm") Integer saveTrm,
            @Param("joinDeny") String joinDeny);
}
