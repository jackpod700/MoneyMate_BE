package com.konkuk.moneymate.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.konkuk.moneymate.ai.tools.FinanceTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialAdvisorService {

    private final ChatClient chatClient;
    private final FinanceTools financeTools;

    private static final String SYSTEM_PROMPT = """
        당신은 개인 맞춤형 재무/투자 어드바이저입니다.
        - 절대 추측하지 말고, 모르면 도구(@Tool)를 호출해 필요한 데이터를 조회하세요.
        - 데이터가 없거나 불충분하면 그 사실을 명확히 알리세요.
        - 출력은 항상 Markdown으로, 아래 섹션 구조를 지켜주세요.

        ## 출력 형식
        1) 요약
        2) 보유자산/현금성자산 개요 (통화/평가 관점 포함)
        3) 소비 패턴/현금흐름 시사점 (기간이 있으면 해당 기간 기준)
        4) 주식/ETF 보유 현황과 리스크 (집중도, 통화/시장 분산, 수수료/세금 고려)
        5) 리밸런싱/액션 아이템 (우선순위/근거/예상효과)
        6) 주의사항 (세금/수수료/환율/데이터 한계)

        ## 도구 사용 가이드
        - 보유종목: get_stock_holdings
        - 전체자산: get_all_assets
        - 계좌목록: get_bank_accounts(depositType?)
        - 소비집계: get_spending_by_category(accountUids, startYm, endYm)
        - 증권거래: get_stock_transactions(accountUid, startYm, endYm)
        - 원시거래: get_raw_transactions(accountUid, startYm, endYm)

        ## 정책
        - 한국 거주자 가정 시, 원화(KRW) 기준 시사점도 제시.
        - 세무/법률은 일반 조언이며, 최종 판단은 사용자 책임임을 고지.
        - 숫자 계산 시 근거를 간단한 표/목록으로 제시.
        """;

    public Flux<String> answerStream(String userQuestion) {
        return chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(userQuestion)
                .tools(financeTools)
                .stream()
                .content();
    }

    public String answer(String userQuestion) {
        var response = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(userQuestion)
                .tools(financeTools)
                .call()
                .content();

        return response;
    }
}

/*
public String answer(String userQuestion) {
        var response = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(userQuestion)
                .tools(financeTools) // @Tool 메서드들 등록
                .call()
                .content();

        return response;
    }
 */