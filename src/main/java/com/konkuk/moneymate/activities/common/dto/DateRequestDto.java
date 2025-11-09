package com.konkuk.moneymate.activities.common.dto;

import lombok.*;
import java.time.LocalDate;

/**
 * <h3>DateRequestDto : 날짜 전달용 DTO</h3>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateRequestDto {
    /**
     * 서버 사이드에서 날짜 계산할 경우
     * WEEK, MONTH, 3MONTH, 6MONTH, YEAR 등으로 각각 처리
     */
    private String type;  // 조회 날짜 타입

    /**
     * 애플리케이션에서 날짜를 계산해서 요청하는 경우
     * 또는 직접 날짜를 지정해서 요청하는 경우
     */
    private LocalDate startDate;
    private LocalDate endDate;
}