package com.konkuk.moneymate.activities.stats.repository;

import com.konkuk.moneymate.activities.stats.entity.KosisStatsIncomeData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface KosisStatsIncomeDataRepository extends CrudRepository<KosisStatsIncomeData, Long> {
    List<KosisStatsIncomeData> findByC1AndC2NameAndItemIdAndYear(String c1, String c2, String itemId, Integer year);
    List<KosisStatsIncomeData> findByC1AndC2NameAndItemIdInAndYear(
            String c1, String c2Name, List<String> itemIds, Integer year
    );

    List<KosisStatsIncomeData> findByC1AndC2NameInAndItemIdInAndYear(
            String c1,
            List<String> c2Names,
            List<String> itemIds,
            Integer year
    );
}
