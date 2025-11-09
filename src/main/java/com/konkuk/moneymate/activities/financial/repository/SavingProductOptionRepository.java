package com.konkuk.moneymate.activities.financial.repository;

import com.konkuk.moneymate.activities.financial.entity.SavingProductOption;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingProductOptionRepository extends JpaRepository<SavingProductOption, UUID> {
}
