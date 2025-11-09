package com.konkuk.moneymate.activities.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockHoldingResponseDto {
    // /asset/stock 의 응답 DTO
    private String accountName;
    private String stockName;
    private String ticker;
    private String quantity;
    private String totalPrice; // 필드명을 'totalPrice'로 변경
    private String profit;
}
