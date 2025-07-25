package com.konkuk.moneymate.auth.templates;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
public class NaverStockProxyController {
    private static final HttpClient client = HttpClient.newHttpClient();


    @GetMapping("/api/proxy/naver-stock/realtime")
    public ResponseEntity<String> getRealtimeStock(@RequestParam String ticker,
                                                   @RequestParam String region,
                                                   @RequestParam String exchange) {
        String url;
        if ("KR".equalsIgnoreCase(region)) {
            url = "https://m.stock.naver.com/api/stock/" + ticker + "/basic";
        } else {
            url = "https://api.stock.naver.com/stock/" + ticker + "." + exchange + "/basic";
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
        String category = type.toLowerCase();

        if (!exchange.matches("NYSE|NASDAQ|AMEX")) {
            return ResponseEntity.badRequest().body("Invalid name");
        }

        if (!category.matches("market|up|down|dividend")) {
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

}