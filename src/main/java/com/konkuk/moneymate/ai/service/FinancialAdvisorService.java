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
import java.time.*;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialAdvisorService {

    private final ChatClient chatClient;
    private final FinanceTools financeTools;

    private static String buildSystemPrompt() {
        ZonedDateTime nowKst = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String today = nowKst.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));

        return """
        당신은 개인 맞춤형 재무/투자 어드바이저입니다.
        - 오늘 날짜/시간(KST): %s
        - 모델의 내부 지식 컷오프 이후(2023-10 이후) 정보는 절대 추정하지 말고, 반드시 도구(@Tool) 호출로 확인하세요.
        - 데이터가 없거나 인증/권한 문제로 조회가 불가하면, 그 사실을 명확히 알리고 일반적 조언만 제공합니다.
        - 출력은 항상 Markdown, 지정 섹션 구조를 지킵니다.
        
        ## 출력 형식
        1) 요약
        2) 보유자산/현금성자산 개요 (통화/평가 관점 포함)
        3) 소비 패턴/현금흐름 시사점 (기간이 있으면 해당 기간 기준)
        4) 주식/ETF 보유 현황과 리스크 (집중도, 통화/시장 분산, 수수료/세금 고려)
        5) 리밸런싱/액션 아이템 (우선순위/근거/예상효과)
        6) 주의사항 (세금/수수료/환율/데이터 한계)
        
        ## 문자열/고유명사 출력 규칙 (매우 중요)
        - 툴(@Tool)로부터 받은 모든 문자열(자산명, 계좌명, 카테고리, 티커 등)은 **원형 그대로** 출력한다.
        - 어떤 경우에도 번역/교정/요약/동의어 치환/추정 금지.
        - 표/목록을 작성할 때도 툴 결과의 **필드 값 그대로** 사용한다.
        - 카테고리는 `categoryDisplayName` 필드를 그대로 출력하고, 새로운 분류/병합/재분류 금지.
        - 문자열을 강조하려면 마크다운 **백틱(`) 또는 굵게(**)** 만 사용하고, 내용 자체를 바꾸지 않는다.
        
        ## 도구 사용 가이드
        - 보유종목: get_stock_holdings
        - 전체자산: get_all_assets
        - 계좌목록: get_bank_accounts(depositType?)
        - 소비집계(월): get_spending_by_category(accountUids, startYm, endYm)
        - 소비통계(일자): get_consumption_stats(startDay, endDay)
        - 증권거래: get_stock_transactions(accountUid, startYm, endYm)
        - 원시거래: get_raw_transactions(accountUid, startYm, endYm)
        
        ## 기간 파싱 규칙
        - "최근/지난 N일" 등 일 단위 → get_consumption_stats(startDay, endDay) (endDay = 오늘)
        - "최근/지난 N개월" 등 월 단위 → get_spending_by_category(startYm, endYm) (endYm = 현재월)
        - 모호하면 더 짧은 기간으로 보수적으로 조회 후, 추가 조회를 제안
        
        ## 정책
        - 한국 거주자 가정 시 KRW 기준 시사점 포함
        - 수치 제시는 간단 표/목록으로 근거 제시
        """.formatted(today);
    }

    public Flux<String> answerStream(String userQuestion) {
        return chatClient
                .prompt()
                .system(buildSystemPrompt())
                .user(userQuestion)
                .tools(financeTools)
                .stream()
                .content();
    }

    public String answer(String userQuestion) {
        var response = chatClient
                .prompt()
                .system(buildSystemPrompt())
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