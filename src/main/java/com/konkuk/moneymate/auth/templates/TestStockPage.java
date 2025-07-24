package com.konkuk.moneymate.auth.templates;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class TestStockPage {

    @GetMapping("/test/page/stock")
    public String showStockPage() {
        return "test-stockpage";
    }

    @GetMapping("/proxy/naver/stock")
    public ResponseEntity<String> getNaverStock(@RequestParam String ticker) {
        String url = "https://m.stock.naver.com/api/stock/" + ticker + "/basic";
        RestTemplate restTemplate = new RestTemplate();
        return ResponseEntity.ok(restTemplate.getForObject(url, String.class));
    }


}
