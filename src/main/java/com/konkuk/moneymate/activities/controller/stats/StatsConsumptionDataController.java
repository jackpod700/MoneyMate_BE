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
 * <h3>StatsConsumptionDataController</h3>
 * <li><strong>searchConsumptionData</strong> : 소비 통계 데이터 조회</li>
 * <b>Params:</b>  StatsDataRequest 참고
 */
@RequiredArgsConstructor
@RestController
public class StatsConsumptionDataController {
    private final StatsAssetService statsAssetService;

    @GetMapping("/stats/data/consumption")
    public ResponseEntity<?> searchConsumptionData(@ModelAttribute StatsDataRequest statsDataRequest, HttpServletRequest request) {
        return statsAssetService.searchConsumptionData(statsDataRequest, request);
    }
}
