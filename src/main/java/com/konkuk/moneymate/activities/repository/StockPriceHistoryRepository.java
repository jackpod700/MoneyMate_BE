package com.konkuk.moneymate.activities.repository; // repository 패키지는 알맞게 수정해주세요.

import com.konkuk.moneymate.activities.entity.StockPriceHistory;
import com.konkuk.moneymate.activities.entity.StockPriceHistoryId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StockPriceHistoryRepository extends JpaRepository<StockPriceHistory, StockPriceHistoryId> {
    //특정 주식의 모든 가격 기록을 날짜순으로 조회
    List<StockPriceHistory> findByISINOrderByDateDesc(String isin);

    @Modifying
    @Transactional
    @Query(value = "INSERT IGNORE INTO stock_price_history " +
            "(isin, date, open_price, high_price, low_price, end_price) " +
            "VALUES (:isin, :date, :open, :high, :low, :close)",
            nativeQuery = true)
    void insertIgnore(@Param("isin") String isin,
                      @Param("date") LocalDate date,
                      @Param("open") BigDecimal open,
                      @Param("high") BigDecimal high,
                      @Param("low") BigDecimal low,
                      @Param("close") BigDecimal close);
}