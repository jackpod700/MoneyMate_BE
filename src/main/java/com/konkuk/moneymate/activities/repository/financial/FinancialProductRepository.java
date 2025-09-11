package com.konkuk.moneymate.activities.repository.financial;

import com.konkuk.moneymate.activities.entity.financialProduct.DepositProduct;
import com.konkuk.moneymate.activities.entity.financialProduct.FinancialProduct;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialProductRepository extends JpaRepository<FinancialProduct, UUID> {
}
