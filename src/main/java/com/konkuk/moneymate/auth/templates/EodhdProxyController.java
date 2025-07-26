package com.konkuk.moneymate.auth.templates;

import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/proxy/eodhd")
@RequiredArgsConstructor
public class EodhdProxyController {
    private final HttpClient client = HttpClient.newHttpClient();

    @GetMapping("/realtime/15min")
    public ResponseEntity<String> realtime(
            @RequestParam String ticker,
            @RequestParam String market) {
        String apiToken = "687f9cc717eea0.26361602";
        String url = String.format(
                "https://eodhd.com/api/real-time/%s.%s?api_token=%s&fmt=json",
                ticker, market, apiToken
        );

        return fetch(url);
    }

    /**
     * Bulk – 전종목 시세 (마지막 영업일)
     */
    @GetMapping("/bulk")
    public ResponseEntity<String> bulk(
            @RequestParam String market) {
        String apiToken = "687f9cc717eea0.26361602";
        String url = String.format(
                "https://eodhd.com/api/eod-bulk-last-day/%s?api_token=%s&fmt=json",
                market, apiToken
        );
        return fetch(url);
    }

    private ResponseEntity<String> fetch(String url) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity
                    .status(resp.statusCode())
                    .body(resp.body());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("Error fetching " + url + ": " + e.getMessage());
        }
    }


}

