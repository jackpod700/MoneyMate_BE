package com.konkuk.moneymate.activities.financial.repository;

import com.konkuk.moneymate.activities.financial.entity.RentHouseLoanProductOption;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentHouseLoanProductOptionRepository extends JpaRepository<RentHouseLoanProductOption, UUID> {

}
