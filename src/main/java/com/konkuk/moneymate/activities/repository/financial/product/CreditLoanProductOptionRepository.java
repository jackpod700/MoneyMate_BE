package com.konkuk.moneymate.activities.repository.financial.product;

import com.konkuk.moneymate.activities.entity.financial.product.CreditLoanProductOption;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditLoanProductOptionRepository extends JpaRepository<CreditLoanProductOption, UUID> {
}
