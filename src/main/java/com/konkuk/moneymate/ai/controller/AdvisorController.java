package com.konkuk.moneymate.ai.controller;


import com.konkuk.moneymate.ai.service.BasicAdvisorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdvisorController {

    private final BasicAdvisorService advisorService;

    /**
     * 예) payload: { "question": "지난 3개월 소비 트렌드와 리밸런싱 제안 알려줘" }
     * JWT는 Security 필터에서 인증된 상태
     */



    @PostMapping("/api/ai/advisor/ask")
    public ResponseEntity<?> ask(@RequestBody AskRequest body, HttpServletRequest req) {
        String answer = advisorService.answer(body.question());
        return ResponseEntity.ok(new AskResponse(answer));
    }




    /*
    @GetMapping("/ai-summary/finance")
    public ResponseEntity<?> askFinance(HttpServletRequest req) {
        return BasicAdvisorService.askFinance(req);
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