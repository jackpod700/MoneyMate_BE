package com.konkuk.moneymate.common.templates;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
// @Component // 서버실행 느려짐
public class    MarketValueRankingRefresher {

    private final RestTemplate rest;
    private final ObjectMapper mapper;
    private final RedisTemplate<String, String> redis;

    private static final String ZKEY = "stocks:%s";          // sorted set: stocks:NASDAQ, stocks:KOSPI 등
    private static final String HKEY = "stockRank:%s:%s";    // hash: stockRank:NASDAQ:NVDA.O 등

    private static final int MAX_RANK  = 100;
    private static final int PAGE_SIZE = 20;

    // 외국 주식 API base
    @Value("${naver.stock.api.base:https://api.stock.naver.com/stock/exchange}")
    private String foreignApiBase;

    // 국내 주식 API base
    @Value("${naver.stock.domestic.api.base:https://m.stock.naver.com/api/stocks/marketValue}")
    private String domesticApiBase;

    @PostConstruct
    public void init() {
        refreshAllExchanges();
    }

    /** 30분마다 다섯 개 거래소(3 foreign + 2 domestic) 순위 갱신 */
    @Scheduled(cron = "0 */30 * * * *")
    public void refreshAllExchanges() {
        for (String ex : List.of("NASDAQ", "NYSE", "AMEX", "KOSPI", "KOSDAQ")) {
            refreshExchange(ex);
        }
    }

    private void refreshExchange(String exchange) {
        String zKey = String.format(ZKEY, exchange);
        redis.delete(zKey);

        // JSON에서 reutersCode + marketValue 함께 읽어옴
        List<StockInfo> top100 = fetchTop100(exchange);

        var zsetOps = redis.opsForZSet();
        int rank = 1;
        for (StockInfo info : top100) {
            // 1) Sorted Set 에 멤버=reutersCode, score=rank 로 저장
            zsetOps.add(zKey, info.reutersCode, rank);

            // 2) Hash 에 rank, exchange, reutersCode, marketValue 저장
            String hKey = String.format(HKEY, exchange, info.reutersCode);
            redis.opsForHash().put(hKey, "rank",        String.valueOf(rank));
            redis.opsForHash().put(hKey, "exchange",    exchange);
            redis.opsForHash().put(hKey, "reutersCode", info.reutersCode);
            redis.opsForHash().put(hKey, "marketValue", String.valueOf(info.marketValue));

            rank++;
        }
    }

    /**
     * exchange 에 따라 적절한 API를 호출해,
     * 순서대로 reutersCode와 marketValue를 담은 리스트 반환.
     */
    private List<StockInfo> fetchTop100(String exchange) {
        List<StockInfo> list = new ArrayList<>(MAX_RANK);
        int pages = (MAX_RANK + PAGE_SIZE - 1) / PAGE_SIZE;

        for (int page = 1; page <= pages && list.size() < MAX_RANK; page++) {
            String url;
            if ("KOSPI".equals(exchange) || "KOSDAQ".equals(exchange)) {
                url = String.format("%s/all?page=%d&pageSize=%d",
                        domesticApiBase, page, PAGE_SIZE);
            } else {
                url = String.format("%s/%s/marketValue?page=%d&pageSize=%d",
                        foreignApiBase, exchange, page, PAGE_SIZE);
            }

            try {
                String json = rest.getForObject(url, String.class);
                JsonNode stocks = mapper.readTree(json).path("stocks");
                if (!stocks.isArray()) break;

                for (JsonNode node : stocks) {
                    if (list.size() >= MAX_RANK) break;

                    String code   = node.path("reutersCode").asText();
                    // "marketValue": 숫자로 변환
                    String mvText = node.path("marketValue").asText("0")
                            .replaceAll(",", "");
                    long   mv     = Long.parseLong(mvText);

                    list.add(new StockInfo(code, mv));
                }
            } catch (Exception e) {
                System.err.printf("Error fetching %s page %d: %s%n",
                        exchange, page, e.getMessage());
            }
        }

        return list;
    }

    /**
     * reutersCode + marketValue(시가총액) 보관용 DTO */
    private static class StockInfo {
        final String reutersCode;
        final long   marketValue;

        StockInfo(String reutersCode, long marketValue) {
            this.reutersCode  = reutersCode;
            this.marketValue = marketValue;
        }
    }
}
