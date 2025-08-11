package com.konkuk.moneymate.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konkuk.moneymate.activities.dto.StockHoldingDto;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

public class StockPriceApiClient {
    private static final String apiUrl= "https://eodhd.com/api/real-time/";
    private static final String parameter = "&api_token=687f9cc717eea0.26361602&fmt=json";

    public static Map<String, BigDecimal> getCurrentPrices(List<StockHoldingDto> stockHoldings) {
        String firstTicker = stockHoldings.getFirst().getTicker() + "." + stockHoldings.getFirst().getExchangeId();
        String multipleTicker = "?s=" + stockHoldings.stream()
                .map(holding -> holding.getTicker() + "." + holding.getExchangeId())
                .collect(Collectors.joining(","));
        String url = apiUrl + firstTicker + multipleTicker + parameter;

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 3. API 요청 및 응답 수신
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. JSON 배열을 DTO 객체의 리스트로 파싱
            List<BatchApiResponseDto> responseList = objectMapper.readValue(response.body(), new TypeReference<>() {});

            // 5. 스트림을 사용하여 리스트를 Map으로 변환
            return responseList.stream()
                    .filter(dto -> dto.getCode() != null && dto.getPreviousClose() != null) // 안전장치
                    .collect(Collectors.toMap(
                            BatchApiResponseDto::getCode,        // Map의 Key
                            BatchApiResponseDto::getPreviousClose, // Map의 Value
                            (existing, replacement) -> existing   // 중복된 Key가 있을 경우 기존 값 유지
                    ));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("실시간 주식 조회 중 오류 발생: ", e);
        }
    }

    /**
     * API 응답 배열의 각 JSON 객체 구조에 맞는 DTO 클래스
     */
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BatchApiResponseDto {
        @JsonProperty("code")
        private String code;

        @JsonProperty("previousClose")
        private BigDecimal previousClose;
    }
}

