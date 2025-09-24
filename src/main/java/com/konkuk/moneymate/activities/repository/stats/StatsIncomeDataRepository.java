package com.konkuk.moneymate.activities.repository.stats;

import com.konkuk.moneymate.activities.entity.stats.StatsConsumptionData;
import com.konkuk.moneymate.activities.entity.stats.StatsIncomeData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StatsIncomeDataRepository extends CrudRepository<StatsIncomeData, Long> {
    List<StatsIncomeData> findByC1AndC2NameAndItemIdAndYear(String c1, String c2, String itemId, Integer year);
    List<StatsIncomeData> findByC1AndC2NameAndItemIdInAndYear(
            String c1, String c2Name, List<String> itemIds, Integer year
    );

    List<StatsIncomeData> findByC1AndC2NameInAndItemIdInAndYear(
            String c1,
            List<String> c2Names,
            List<String> itemIds,
            Integer year
    );
}
