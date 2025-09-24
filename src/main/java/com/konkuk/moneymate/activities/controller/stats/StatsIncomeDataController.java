package com.konkuk.moneymate.activities.controller.stats;

import com.konkuk.moneymate.activities.dto.stats.StatsDataRequest;
import com.konkuk.moneymate.activities.service.stats.StatsAssetService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h3>StatsIncomeDataController</h3>
 * <li><strong>searchIncomeData</strong> : 수입 통계 데이터 조회</li>
 * <b>Params:</b>  StatsDataRequest 참고
 */
@RequiredArgsConstructor
@RestController
public class StatsIncomeDataController {
    private final StatsAssetService statsAssetService;

    @GetMapping("/stats/data/income")
    public ResponseEntity<?> searchIncomeData(@ModelAttribute StatsDataRequest statsDataRequest, HttpServletRequest request) {
        return statsAssetService.searchIncomeData(statsDataRequest, request);
    }
}
