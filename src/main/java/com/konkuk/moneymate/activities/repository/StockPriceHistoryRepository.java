package com.konkuk.moneymate.activities.repository; // repository 패키지는 알맞게 수정해주세요.

import com.konkuk.moneymate.activities.entity.StockPriceHistory;
import com.konkuk.moneymate.activities.entity.StockPriceHistoryId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockPriceHistoryRepository extends JpaRepository<StockPriceHistory, StockPriceHistoryId> {
    //특정 주식의 모든 가격 기록을 날짜순으로 조회
    List<StockPriceHistory> findByISINOrderByDateDesc(String isin);
}