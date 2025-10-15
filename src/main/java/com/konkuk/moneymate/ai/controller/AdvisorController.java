package com.konkuk.moneymate.ai.controller;


import com.konkuk.moneymate.ai.service.*;
import com.konkuk.moneymate.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdvisorController {

    private final BasicAdvisorService advisorService;
    private final AssetAdvisorService assetAdvisorService;
    // private final FinancialAdvisorService financialAdvisorService;
    private final ConsumptionAdvisorService consumptionAdvisorService;
    private final InvestAdvisorService investAdvisorService;
    private final TransactionAdvisorService transactionAdvisorService;

    /**
     * 예) payload: { "question": "지난 3개월 소비 트렌드와 리밸런싱 제안 알려줘" }
     * JWT는 Security 필터에서 인증된 상태
     */



    @PostMapping("/api/ai/advisor/ask")
    public ResponseEntity<?> ask(@RequestBody AskRequest body, HttpServletRequest req) {
        String answer = advisorService.answer(body.question());
        return ResponseEntity.ok(new AskResponse(answer));
    }





    @GetMapping("/ai-summary/asset")
    public ResponseEntity<?> askAsset(@RequestParam Integer year) {
        return assetAdvisorService.askAsset(year);
    }

    /*
    @GetMapping("/ai-summary/finance")
    public ResponseEntity<?> askFinance(HttpServletRequest req) {
        return AssetAdvisorService.askFinance(req);
    }


    @GetMapping("/ai-summary/transaction")
    public ResponseEntity<?> askTransaction(HttpServletRequest req) {
        return BasicAdvisorService.askTransaction(req);
    }

    @GetMapping("/ai-summary/consumption")
    public ResponseEntity<?> askConsumption(HttpServletRequest req) {
        return BasicAdvisorService.askConsumption(req);
    }

    @GetMapping("/ai-summary/invest")
    public ResponseEntity<?> askInvest(HttpServletRequest req) {
        return BasicAdvisorService.askInvest(req);
    }
     */









    public record AskRequest(String question) {}
    public record AskResponse(String answerMarkdown) {}
}


/*
@PostMapping("/ask")
    public ResponseEntity<?> ask(@RequestBody AskRequest body, HttpServletRequest req) {
        String answer = advisorService.answer(body.question());
        return ResponseEntity.ok(new AskResponse(answer));
    }

    @PostMapping(value = "/ask/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> askStream(@RequestBody AskRequest body) {
        return advisorService.answerStream(body.question());
    }
 */