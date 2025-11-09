package com.konkuk.moneymate.activities.financial.repository;

import com.konkuk.moneymate.activities.financial.dto.MortgageLoanProductDto;
import com.konkuk.moneymate.activities.financial.entity.MortgageLoanProduct;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MortgageLoanProductRepository extends JpaRepository<MortgageLoanProduct, UUID> {
    @Query("""
    SELECT new com.konkuk.moneymate.activities.dto.financialProduct.MortgageLoanProductDto(
        mp.financialCompany.name,
        mp.productName,
        mp.joinWay,
        mp.disclosureStartDay,
        mp.disclosureEndDay,
        mp.financialCompany.homeUrl,
        mp.financialCompany.callNum,
        mpo.lendRateTypeName,
        mpo.lendRateMin,
        mpo.lendRateMax,
        mpo.lendRateAvg,
        mp.loanInciExpn,
        mp.earlyRepayFee,
        mp.delayRate,
        mp.loanLimit,
        mpo.mrtgTypeName,
        mpo.rpayTypeName
    )
    FROM MortgageLoanProduct mp
    JOIN mp.options mpo
    WHERE mp.financialCompany.code IN :companyCodes
    AND mpo.mrtgType = :mrtgType
""")
    List<MortgageLoanProductDto> findByFinancialCompanyCodeAndMrtgType(
            @Param("companyCodes") List<String> companyCodes,
            @Param("mrtgType") char mrtgType);
}
