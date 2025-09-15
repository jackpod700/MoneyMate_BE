package com.konkuk.moneymate.activities.repository.financial.product;

import com.konkuk.moneymate.activities.entity.financial.product.FinancialCompanyRegion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialCompanyRegionRepository extends JpaRepository<FinancialCompanyRegion, UUID> {

}
