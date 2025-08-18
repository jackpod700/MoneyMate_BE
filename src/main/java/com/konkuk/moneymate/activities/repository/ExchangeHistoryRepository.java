package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.entity.ExchangeHistory;
import com.konkuk.moneymate.activities.entity.ExchangeHistoryId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeHistoryRepository extends JpaRepository<ExchangeHistory, ExchangeHistoryId> {
    // JpaRepository를 상속받는 것만으로 기본적인 save(), findById(), findAll(), delete() 등
    // 모든 CRUD 메소드가 자동으로 제공됩니다.

    // 필요하다면 아래와 같이 쿼리 메소드를 추가할 수 있습니다.
    // 예: 특정 통화의 모든 기록 조회
    List<ExchangeHistory> findByBaseCurrency(String baseCurrency);
}