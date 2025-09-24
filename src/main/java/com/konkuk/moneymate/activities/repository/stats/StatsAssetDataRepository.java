package com.konkuk.moneymate.activities.repository.stats;

import com.konkuk.moneymate.activities.entity.stats.StatsAssetData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StatsAssetDataRepository extends CrudRepository<StatsAssetData, Long> {
    List<StatsAssetData> findByC1AndC2NameAndItemIdAndYear(String c1, String c2, String itemId, Integer year);
    List<StatsAssetData> findByC1AndC2NameAndItemIdInAndYear(
            String c1, String c2Name, List<String> itemIds, Integer year
    );

    List<StatsAssetData> findByC1AndC2NameInAndItemIdInAndYear(
            String c1,
            List<String> c2Names,
            List<String> itemIds,
            Integer year
    );
    List<StatsAssetData> findByC1AndC2AndYear(String c1, String c2, Integer year);
    List<StatsAssetData> findByC1AndItemIdAndYear(String c1, String itemId, Integer year);
}
