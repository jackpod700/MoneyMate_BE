package com.konkuk.moneymate.activities.repository.financial.product;

import com.konkuk.moneymate.activities.entity.financial.product.SavingProductOption;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingProductOptionRepository extends JpaRepository<SavingProductOption, UUID> {
}
