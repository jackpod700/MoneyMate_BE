package com.konkuk.moneymate.activities.stats.controller;

import com.konkuk.moneymate.activities.assets.dto.AssetHistoryDto;
import com.konkuk.moneymate.activities.stats.service.StatisticService;
import com.konkuk.moneymate.auth.service.JwtService;
import com.konkuk.moneymate.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StatisticController {
    private final JwtService jwtService;
    private final StatisticService statisticService;

    @GetMapping("/asset/stats/history")
    public ResponseEntity<?> getAssetHistory(HttpServletRequest httpServletRequest, @RequestParam("category") String category){
        String userUid = jwtService.getUserUid(httpServletRequest);
        HashMap<YearMonth, BigDecimal> assetHistory;
        switch(category){
            case "total":
                assetHistory = statisticService.getTotalAssetHistory(userUid);
                break;
            case "withdrawal":
                assetHistory = statisticService.getBankAccountHistory(userUid, "입출금");
                break;
            case "deposit":
                assetHistory = statisticService.getBankAccountHistory(userUid, "예적금");
                break;
            case "stock":
                assetHistory = statisticService.getBankAccountHistory(userUid, "증권");
                break;
            case "asset":
                assetHistory = statisticService.getAssetHistory(userUid);
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                null));
        }
        List<AssetHistoryDto> assetHistoryList = Objects.requireNonNull(assetHistory).entrySet().stream()
                .map(entry -> new AssetHistoryDto(entry.getKey(), entry.getValue().toString()))
                .sorted(Comparator.comparing(AssetHistoryDto::getDate)) // 날짜순으로 정렬
                .toList();
        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                        "자산추이조회성공", assetHistoryList)
        );
    }
}
