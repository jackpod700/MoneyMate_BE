package com.konkuk.moneymate.common.templates;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

/**
 * <h3>TestStockPage</h3>
 * <p>주식 시세 조회 테스트 페이지 컨트롤러</p>
 * <li><b>GET /test/page/stock:</b> 주식 테스트 페이지 표시</li>
 * <li><b>GET /proxy/naver/stock:</b> 네이버 주식 API 프록시 (레거시)</li>
 */
@Controller
public class TestStockPage {

    /**
     * <h3>GET /test/page/stock</h3>
     * <p>주식 시세 조회 테스트 페이지를 표시합니다</p>
     * @return Thymeleaf 템플릿 이름 "test-stockpage"
     */
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
