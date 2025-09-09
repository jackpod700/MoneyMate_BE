package com.konkuk.moneymate.activities.repository.financial;

import com.konkuk.moneymate.activities.entity.financialProduct.FinancialCompany;
import com.konkuk.moneymate.activities.entity.financialProduct.FinancialCompanyRegion;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialCompanyRegionRepository extends JpaRepository<FinancialCompanyRegion, UUID> {

}
