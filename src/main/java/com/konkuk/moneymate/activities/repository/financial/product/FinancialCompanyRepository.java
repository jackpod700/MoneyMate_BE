package com.konkuk.moneymate.activities.repository.financial.product;

import com.konkuk.moneymate.activities.entity.financial.product.FinancialCompany;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialCompanyRepository extends JpaRepository<FinancialCompany, String> {
    /**
     * 지역 이름 목록(areaNames)에 해당하는 지점을 하나라도 가진
     * 모든 금융 회사(FinancialCompany)를 중복 없이 조회합니다.
     * @param areaCodes 조회할 지역 코드 리스트
     * @return 조건에 맞는 금융 회사 리스트
     */
    @Query("SELECT DISTINCT fcr.financialCompany " +
            "FROM FinancialCompanyRegion fcr " +
            "WHERE fcr.areaCode IN :areaCodes")
    List<FinancialCompany> findCompaniesByAreaCodes(@Param("areaCodes") List<String> areaCodes);
}
