package com.konkuk.moneymate.activities.repository.financial.product;

import com.konkuk.moneymate.activities.dto.financial.product.RentHouseLoanProductDto;
import com.konkuk.moneymate.activities.entity.financial.product.RentHouseLoanProduct;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RentHouseLoanProductRepository extends JpaRepository<RentHouseLoanProduct, UUID> {
    @Query("""
    SELECT new com.konkuk.moneymate.activities.dto.financial.product.RentHouseLoanProductDto(
        pp.financialCompany.name,
        pp.productName,
        pp.joinWay,
        pp.disclosureStartDay,
        pp.disclosureEndDay,
        pp.financialCompany.homeUrl,
        pp.financialCompany.callNum,
        ppo.lendRateTypeName,
        ppo.lendRateMin,
        ppo.lendRateMax,
        ppo.lendRateAvg,
        pp.loanInciExpn,
        pp.earlyRepayFee,
        pp.delayRate,
        pp.loanLimit,
        ppo.rpayTypeName
    )
    FROM RentHouseLoanProduct pp
    JOIN pp.options ppo
    WHERE pp.financialCompany.code IN :companyCodes
""")
    List<RentHouseLoanProductDto> findByFinancialCompanyCode(
            @Param("companyCodes") List<String> companyCodes);
}
