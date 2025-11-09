package com.konkuk.moneymate.activities.news.repository;

import java.util.List;
import java.util.UUID;

import com.konkuk.moneymate.activities.news.entity.NewsSummarize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsSummarizeRepository extends JpaRepository<NewsSummarize, UUID> {
    /**
     * 가장 최근에 생성된(generatedTime 기준) 데이터를 1건 조회합니다.
     * Optional로 감싸서 결과가 없을 경우(테이블이 비어있을 경우)를 안전하게 처리합니다.
     */
    @Query("""
            SELECT ns FROM NewsSummarize ns
            WHERE ns.generatedTime = (
                SELECT MAX(ns2.generatedTime)
                FROM NewsSummarize ns2
                WHERE ns2.category = ns.category
            )
            """)
    List<NewsSummarize> findFirstOfAllCategoryOrderByGeneratedTimeDesc();
}
