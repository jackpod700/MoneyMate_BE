package com.konkuk.moneymate.activities.repository.financial.product;

import com.konkuk.moneymate.activities.dto.financial.product.CreditLoanProductDto;
import com.konkuk.moneymate.activities.entity.financial.product.CreditLoanProduct;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditLoanProductRepository extends JpaRepository<CreditLoanProduct, UUID> {
    @Query("""
    SELECT new com.konkuk.moneymate.activities.dto.financial.product.CreditLoanProductDto(
        cp.financialCompany.name,
        cp.productName,
        cp.joinWay,
        cp.disclosureStartDay,
        cp.disclosureEndDay,
        cp.financialCompany.homeUrl,
        cp.financialCompany.callNum,
        cp.creditProductTypeName,
        cpo.creditLendRateTypeName,
        cpo.creditGrade1,
        cpo.creditGrade4,
        cpo.creditGrade5,
        cpo.creditGrade6,
        cpo.creditGrade10,
        cpo.creditGrade11,
        cpo.creditGrade12,
        cpo.creditGrade13,
        cp.cbName
    )
    FROM CreditLoanProduct cp
    JOIN cp.options cpo
    WHERE cp.financialCompany.code IN :companyCodes
""")
    List<CreditLoanProductDto> findByFinancialCompanyCode(
            @Param("companyCodes") List<String> companyCodes);
}
