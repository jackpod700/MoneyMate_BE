package com.konkuk.moneymate.activities.stats.controller;

import com.konkuk.moneymate.activities.stats.dto.KosisStatsDataRequest;
import com.konkuk.moneymate.activities.stats.service.KosisStatsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h3>KosisStatsController</h3>
 * <p>또래 통계(KOSIS) 조회 컨트롤러</p>
 * <li><b>GET /asset/stats/asset:</b> 자산통계 데이터 조회</li>
 * <li><b>GET /asset/stats/consumption:</b> 소비통계 데이터 조회</li>
 * <li><b>GET /asset/stats/income:</b> 소득통계 데이터 조회</li>
 *
 */
@RequiredArgsConstructor
@RestController
public class KosisStatsController {
    private final KosisStatsService kosisStatsService;

    @GetMapping("/stats/data/asset")
    public ResponseEntity<?> searchAssetData(@ModelAttribute KosisStatsDataRequest kosisStatsDataRequest, HttpServletRequest request) {
        return kosisStatsService.searchAssetData(kosisStatsDataRequest, request);
    }

    @GetMapping("/stats/data/consumption")
    public ResponseEntity<?> searchConsumptionData(@ModelAttribute KosisStatsDataRequest kosisStatsDataRequest, HttpServletRequest request) {
        return kosisStatsService.searchConsumptionData(kosisStatsDataRequest, request);
    }

    @GetMapping("/stats/data/income")
    public ResponseEntity<?> searchIncomeData(@ModelAttribute KosisStatsDataRequest kosisStatsDataRequest, HttpServletRequest request) {
        return kosisStatsService.searchIncomeData(kosisStatsDataRequest, request);
    }
}
