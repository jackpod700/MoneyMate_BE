package com.konkuk.moneymate.activities.stats.controller;


import com.konkuk.moneymate.activities.bankaccount.service.BankAccountService;
import com.konkuk.moneymate.activities.stats.service.ConsumptionStatsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <h3>ConsumptionStatsController : 소비통계 조회 컨트롤러</h3>
 * <li><b> POST /asset/stats/consumption : 소비 통계 조회 </b></li>
 */
@RequiredArgsConstructor
@RestController
public class ConsumptionStatsController {
    private final BankAccountService bankAccountService;
    private final ConsumptionStatsService consumptionStatsService;


    @GetMapping("/asset/stats/consumption")
    public ResponseEntity<?> consumptionStats(
            @RequestParam("startDay") String startDay,
            @RequestParam("endDay") String endDay,
            HttpServletRequest request) {

        return consumptionStatsService.consumptionStats(startDay, endDay, request);
    }

}
