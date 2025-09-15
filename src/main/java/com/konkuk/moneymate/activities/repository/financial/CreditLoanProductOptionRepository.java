package com.konkuk.moneymate.activities.repository.financial;

import com.konkuk.moneymate.activities.entity.financialProduct.CreditLoanProductOption;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditLoanProductOptionRepository extends JpaRepository<CreditLoanProductOption, UUID> {
}
