package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.entity.ExchangeHistory;
import com.konkuk.moneymate.activities.entity.ExchangeHistoryId;

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
public interface ExchangeHistoryRepository extends JpaRepository<ExchangeHistory, ExchangeHistoryId> {
    // JpaRepository를 상속받는 것만으로 기본적인 save(), findById(), findAll(), delete() 등
    // 모든 CRUD 메소드가 자동으로 제공됩니다.

    // 필요하다면 아래와 같이 쿼리 메소드를 추가할 수 있습니다.
    // 예: 특정 통화의 모든 기록 조회
    List<ExchangeHistory> findByBaseCurrency(String baseCurrency);

    @Modifying
    @Transactional
    @Query(value = "INSERT IGNORE INTO exchange_history " +
            "(base_currency, date, open_price, high_price, low_price, end_price) " +
            "VALUES (:baseCurrency, :date, :openPrice, :highPrice, :lowPrice, :endPrice)",
            nativeQuery = true)
    void insertIgnore(@Param("baseCurrency") String baseCurrency,
                      @Param("date") LocalDate date,
                      @Param("openPrice") BigDecimal openPrice,
                      @Param("highPrice") BigDecimal highPrice,
                      @Param("lowPrice") BigDecimal lowPrice,
                      @Param("endPrice") BigDecimal endPrice);
}