package com.konkuk.moneymate.activities.financial.repository;

import com.konkuk.moneymate.activities.financial.entity.FinancialProduct;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialProductRepository extends JpaRepository<FinancialProduct, UUID> {
}
