package com.konkuk.moneymate.activities.stock.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konkuk.moneymate.activities.stock.entity.Stock;
import com.konkuk.moneymate.activities.stock.repository.ExchangeHistoryRepository;
import com.konkuk.moneymate.activities.stock.repository.StockPriceHistoryRepository;
import com.konkuk.moneymate.activities.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockLastdayFetchService {

    private final StockRepository stockRepository;
    private final ExchangeHistoryRepository exchangeHistoryRepository;
    private final StockPriceHistoryRepository stockPriceHistoryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${eodhd.api.key}")
    private String apiKey;

    /**
     * Quartz Job 에서 호출되는 진입 메서드
     */
    public void fetchLastdayPrices() {
        log.info("=== Stock price fetch job started ===");

        List<Stock> stocks = stockRepository.findAll();

        /**
         * 주식 Fetch
         */
        for (Stock stock : stocks) {
            try {
                if ("KO".equalsIgnoreCase(stock.getExchangeId())) {
                    fetchDomesticStock(stock);
                } else {
                    fetchForeignStock(stock);
                }
            } catch (Exception e) {
                log.error("[FAIL] {}.{} [{}] : {}", stock.getTicker(),
                        stock.getExchangeId(), stock.getName(), e.getMessage());
            }
        }

        /**
         * 환율 Fetch
         */
        try {
            fetchExchangeHistory("USD"); // USD/KRW
            fetchExchangeHistory("EUR"); // EUR/KRW
        } catch (Exception e) {
            log.error("Forex insert FAILED : {}", e.getMessage());
        }

        log.info("=== Stock price fetch job completed ===");
    }

    /**
     * 국내 주식 (KO)
     */
    private void fetchDomesticStock(Stock stock) throws Exception {
        ZonedDateTime yesterday = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusDays(1);

        String endDateTime = yesterday
                .withHour(23).withMinute(59)
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));

        String startDateTime = yesterday.minusWeeks(1)
                .withHour(0).withMinute(0)
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));

        String apiUrl = String.format(
                "https://api.stock.naver.com/chart/domestic/item/%s/day?startDateTime=%s&endDateTime=%s",
                stock.getTicker(),
                startDateTime,
                endDateTime
        );

        String response = new java.util.Scanner(new java.net.URL(apiUrl).openStream(), "UTF-8")
                .useDelimiter("\\A").next();

        JsonNode root = objectMapper.readTree(response);

        for (JsonNode node : root) {
            LocalDate date = LocalDate.parse(node.get("localDate").asText(),
                    DateTimeFormatter.ofPattern("yyyyMMdd"));
            BigDecimal open = node.get("openPrice").decimalValue();
            BigDecimal high = node.get("highPrice").decimalValue();
            BigDecimal low = node.get("lowPrice").decimalValue();
            BigDecimal close = node.get("closePrice").decimalValue();

            stockPriceHistoryRepository.insertIgnore(
                    stock.getISIN(), date, open, high, low, close
            );
        }

        log.info("[OK] {}.{} [{}] Domestic data saved ({})",
                stock.getTicker(), stock.getExchangeId(), stock.getName(), root.size());
    }

    /**
     * 해외 주식 (기타)
     */
    private void fetchForeignStock(Stock stock) throws Exception {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusWeeks(1);

        String apiUrl = String.format(
                "https://eodhd.com/api/eod/%s.%s?api_token=%s&fmt=json&from=%s&to=%s",
                stock.getTicker(),
                stock.getExchangeId(),
                apiKey,
                start.toString(),
                end.toString()
        );

        String response = new java.util.Scanner(new java.net.URL(apiUrl).openStream(), "UTF-8")
                .useDelimiter("\\A").next();

        JsonNode root = objectMapper.readTree(response);

        for (JsonNode node : root) {
            LocalDate date = LocalDate.parse(node.get("date").asText());
            BigDecimal open = node.get("open").decimalValue();
            BigDecimal high = node.get("high").decimalValue();
            BigDecimal low = node.get("low").decimalValue();
            BigDecimal close = node.get("close").decimalValue();

            stockPriceHistoryRepository.insertIgnore(
                    stock.getISIN(), date, open, high, low, close
            );
        }

        log.info("[OK] {}.{} [{}] Foreign data saved ({})",
                stock.getTicker(), stock.getExchangeId(), stock.getName(), root.size());
    }

    /**
     * 환율 데이터
     */
    public void fetchExchangeHistory(String baseCurrency) throws Exception {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusWeeks(1);

        String apiUrl = String.format(
                "https://eodhd.com/api/eod/%sKRW.FOREX?api_token=%s&fmt=json&from=%s&to=%s",
                baseCurrency,
                apiKey,
                start.toString(),
                end.toString()
        );

        String response = new java.util.Scanner(new java.net.URL(apiUrl).openStream(), "UTF-8")
                .useDelimiter("\\A").next();

        JsonNode root = objectMapper.readTree(response);

        int count = 0;
        for (JsonNode node : root) {
            LocalDate date = LocalDate.parse(node.get("date").asText());
            BigDecimal open = node.get("open").decimalValue();
            BigDecimal high = node.get("high").decimalValue();
            BigDecimal low = node.get("low").decimalValue();
            BigDecimal close = node.get("close").decimalValue();

            exchangeHistoryRepository.insertIgnore(
                    baseCurrency, date, open, high, low, close
            );

            count++;
        }

        log.info("[OK] {} 환율 데이터 저장 완료 ({} rows)", baseCurrency, count);
    }

}
