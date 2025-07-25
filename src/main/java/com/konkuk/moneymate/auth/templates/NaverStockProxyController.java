package com.konkuk.moneymate.auth.templates;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
            {
                try {
                    HttpClient client = HttpClient.newHttpClient();
                    ObjectMapper mapper = new ObjectMapper();

                    // 1) KOSPI, KOSDAQ 각각 상위 pageSize 종목 가져오기
                    List<StockItem> all = new ArrayList<>();
                    for (String ex : List.of("KOSPI", "KOSDAQ")) {
                        String apiUrl = baseUrl + "marketValue/" + ex
                                + "?page=" + page + "&pageSize=" + pageSize;
                        HttpRequest req = HttpRequest.newBuilder()
                                .uri(URI.create(apiUrl))
                                .header("User-Agent", "Mozilla/5.0")
                                .build();
                        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

                        JsonNode stocks = mapper.readTree(resp.body()).path("stocks");
                        if (stocks.isArray()) {
                            for (JsonNode node : stocks) {
                                String code = node.path("reutersCode").asText();
                                long mv = Long.parseLong(
                                        node.path("marketValue")
                                                .asText("0")
                                                .replaceAll(",", "")
                                );
                                all.add(new StockItem(code, mv));
                            }
                        }
                    }

                    // 2) marketValue 기준 내림차순 정렬 후 상위 20개 선택
                    List<String> topCodes = all.stream()
                            .sorted(Comparator.comparingLong(StockItem::getMarketValue).reversed())
                            .limit(20)
                            .map(StockItem::getReutersCode)
                            .collect(Collectors.toList());

                    // 3) "%2C" 로 join 한 뒤 실시간 시세 API 호출
                    String codesParam = String.join("%2C", topCodes);
                    String pollingUrl = "https://polling.finance.naver.com/api/realtime/domestic/stock/" + codesParam;

                    HttpRequest pollingReq = HttpRequest.newBuilder()
                            .uri(URI.create(pollingUrl))
                            .header("User-Agent", "Mozilla/5.0")
                            .build();
                    HttpResponse<String> pollingResp = client.send(pollingReq, HttpResponse.BodyHandlers.ofString());

                    return ResponseEntity.ok(pollingResp.body());
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body("Error fetching market data: " + e.getMessage());
                }
            }
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

}