package com.konkuk.moneymate.activities.repository.stats;

import com.konkuk.moneymate.activities.entity.stats.StatsConsumptionData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatsConsumptionDataRepository extends JpaRepository<StatsConsumptionData, Long> {
    List<StatsConsumptionData> findByC1AndC2NameAndItemIdAndYear(String c1, String c2, String itemId, Integer year);
    List<StatsConsumptionData> findByC1AndC2NameAndItemIdInAndYear(
            String c1, String c2Name, List<String> itemIds, Integer year
    );

    List<StatsConsumptionData> findByC1AndC2NameInAndItemIdInAndYear(
            String c1,
            List<String> c2Names,
            List<String> itemIds,
            Integer year
    );
}
