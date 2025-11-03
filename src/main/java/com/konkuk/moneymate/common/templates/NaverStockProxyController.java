package com.konkuk.moneymate.common.templates;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * <h3>NaverStockProxyController</h3>
 * <p>네이버 증권 API에 대한 프록시 컨트롤러 (CORS 우회)</p>
 * <li><b>GET /api/proxy/naver-stock/realtime:</b> 실시간 주식 시세</li>
 * <li><b>GET /api/proxy/naver-stock/index:</b> 시장 지수 (국장/미장/세계)</li>
 * <li><b>GET /api/proxy/naver-stock/domestic:</b> 국내 주식 목록</li>
 * <li><b>GET /api/proxy/naver-stock/ex:</b> 해외 주식 목록</li>
 * <li><b>GET /api/proxy/naver-stock/exchange:</b> 환율 정보</li>
 */
@Slf4j
@RestController
public class NaverStockProxyController {
    private static final HttpClient client = HttpClient.newHttpClient();


    @GetMapping("/api/proxy/naver-stock/realtime")
    public ResponseEntity<String> getRealtimeStock(@RequestParam String ticker,
                                                   @RequestParam String region,
                                                   @RequestParam String exchange) {
        HttpClient client = HttpClient.newHttpClient();

        log.info("[Entry] getRealtimeStock region={}, ticker={}, exchange={}", region, ticker, exchange);
        if ("KR".equalsIgnoreCase(region)) {
            String url = "https://m.stock.naver.com/api/stock/" + ticker + "/basic";
            log.info("[KR] fetching → {}", url);
            return fetch(client, url);
        }

        String primaryUrl  = "https://api.stock.naver.com/stock/" + ticker + "." + exchange + "/basic";
        String fallbackUrl = "https://api.stock.naver.com/stock/" + ticker + "/basic";

        ResponseEntity<String> primaryResponse = fetch(client, primaryUrl);
        if (primaryResponse.getStatusCode().is2xxSuccessful()) {
            return primaryResponse;
        }

        ResponseEntity<String> fallbackResponse = fetch(client, fallbackUrl);
        if (fallbackResponse.getStatusCode().is2xxSuccessful()) {
            return fallbackResponse;
        }

        return primaryResponse;
    }




    /**
     * 국장 개요
     * https://api.stock.naver.com/index/nation/KOR
     *
     * 미장 개요
     * https://api.stock.naver.com/index/nation/USA
     *
     * 전세계 주요
     * https://api.stock.naver.com/index/major
     */

    @GetMapping("/api/proxy/naver-stock/index")
    public ResponseEntity<?> getStockIndex(@RequestParam String nation) {
        String url;

        switch (nation.toUpperCase()) {
            case "KOR":
                url = "https://api.stock.naver.com/index/nation/KOR";
                break;
            case "USA":
                url = "https://api.stock.naver.com/index/nation/USA";
                break;
            case "MAJOR":
                url = "https://api.stock.naver.com/index/major";
                break;
            default:
                return ResponseEntity.badRequest()
                        .body("Invalid Nation: " + nation);
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.ok(response.body());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error: " + e.getMessage());
        }
    }

    /**
     * 국내주식
     */
    @GetMapping("/api/proxy/naver-stock/domestic")
    public ResponseEntity<?> getStockIndex(@RequestParam String type,
                                           @RequestParam String page,
                                           @RequestParam String pageSize) {

        String baseUrl = "https://m.stock.naver.com/api/stocks/";
        String url;

        switch (type.toLowerCase()) {
            case "market": // 시가총액
                url = baseUrl + "marketValue/all?page=" + page + "&pageSize=" + pageSize;
                break;
            case "up": // 상승
                url = baseUrl + "up/all?page=" + page + "&pageSize=" + pageSize;
                break;
            case "down": // 하락
                url = baseUrl + "down/all?page=" + page + "&pageSize=" + pageSize;
                break;
            case "search": // 인기검색
                url = baseUrl + "searchTop/all?page=" + page + "&pageSize=" + pageSize;
                break;
            case "industry": // 업종
                url = baseUrl + "industry?page=" + page + "&pageSize=" + pageSize;
                break;
            case "theme": // 테마
                url = baseUrl + "theme?page=" + page + "&pageSize=" + pageSize;
                break;
            default:
                return ResponseEntity.badRequest().body("Invalid type");
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.ok(response.body());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error: " + e.getMessage());
        }

    }

    /**
     * 해외주식
     */
    @GetMapping("/api/proxy/naver-stock/ex")
    public ResponseEntity<?> getStockIndexEx(@RequestParam String name,
                                           @RequestParam String type,
                                           @RequestParam String page,
                                           @RequestParam String pageSize) {

        String exchange = name.toUpperCase();
        String category = type;

        if (!exchange.matches("NYSE|NASDAQ|AMEX")) {
            return ResponseEntity.badRequest().body("Invalid name");
        }

        if (!category.matches("market|marketValue|up|down|dividend")) {
            return ResponseEntity.badRequest().body("Invalid type");
        }

        String url = String.format(
                "https://api.stock.naver.com/stock/exchange/%s/%s?page=%s&pageSize=%s",
                exchange, category, page, pageSize
        );


        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.ok(response.body());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error: " + e.getMessage());
        }
    }


    /**
     * 환율
     */
    @GetMapping("/api/proxy/naver-stock/exchange")
    public ResponseEntity<?> getExchangeIndex() {
        String url = "https://m.stock.naver.com/front-api/marketIndex/exchange/new";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.ok(response.body());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error: " + e.getMessage());
        }
    }












    /**
     * 주어진 URL을 User-Agent 헤더와 함께 GET 요청하고,
     * 예외가 터지면 502로 감싸서 리턴해 주는 헬퍼
     */
    private ResponseEntity<String> fetch(HttpClient client, String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity
                    .status(resp.statusCode())
                    .body(resp.body());
        } catch (IOException | InterruptedException e) {
            // 스레드 상태 복원을 위해 다시 인터럽트
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("Error fetching " + url + ": " + e.getMessage());
        }
    }

}