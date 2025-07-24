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


    @GetMapping("/api/naver/realtime")
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
                    .header("User-Agent", "Mozilla/5.0")  // 필요한 경우
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.ok(response.body());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error: " + e.getMessage());
        }
    }
}