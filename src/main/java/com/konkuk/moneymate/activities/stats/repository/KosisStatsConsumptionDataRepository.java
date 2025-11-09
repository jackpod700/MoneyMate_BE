package com.konkuk.moneymate.activities.stats.repository;

import com.konkuk.moneymate.activities.stats.entity.KosisStatsConsumptionData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KosisStatsConsumptionDataRepository extends JpaRepository<KosisStatsConsumptionData, Long> {
    List<KosisStatsConsumptionData> findByC1AndC2NameAndItemIdAndYear(String c1, String c2, String itemId, Integer year);
    List<KosisStatsConsumptionData> findByC1AndC2NameAndItemIdInAndYear(
            String c1, String c2Name, List<String> itemIds, Integer year
    );

    List<KosisStatsConsumptionData> findByC1AndC2NameInAndItemIdInAndYear(
            String c1,
            List<String> c2Names,
            List<String> itemIds,
            Integer year
    );
}
