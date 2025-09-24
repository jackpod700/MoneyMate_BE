package com.konkuk.moneymate.activities.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <h3>StatsDataRequest  (DTO Class)</h3>  <br>
 * age: 본인의 만나이 <br>
 * c2: category2 자산, 부채, 경상소득 등 c2_nm<br>
 * itemId: 평균,중앙값,비율 (예시: 평균, 중앙값, 비율)<br>
 * year: 수록연도 (2024)<br>
 */
@AllArgsConstructor
@Data
public class StatsDataRequest {
    private Integer age;
    private String c2;
    private String itemId;
    private Integer year;
}
