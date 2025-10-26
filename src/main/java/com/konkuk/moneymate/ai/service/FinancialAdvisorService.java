package com.konkuk.moneymate.ai.service;

import com.konkuk.moneymate.ai.tools.FinancialTools;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialAdvisorService {
    private final ChatClient chatClient;
    private final FinancialTools financialTools;

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
        3) 최근 1년간 자산 변동 추이
        4) 소비 패턴/현금흐름 시사점 (기간이 있으면 해당   기간 기준)
        5) 주식/ETF 보유 현황과 리스크 (집중도, 통화/시장 분산, 수수료/세금 고려)
        6) 리밸런싱/액션 아이템 (우선순위/근거/예상효과)
        7) 주의사항 (세금/수수료/환율/데이터 한계)
        
        ## 문자열/고유명사 출력 규칙 (매우 중요)
        - 툴(@Tool)로부터 받은 모든 문자열(자산명, 계좌명, 카테고리, 티커 등)은 **원형 그대로** 출력한다.
        - 어떤 경우에도 번역/교정/요약/동의어 치환/추정 금지.
        - 표/목록을 작성할 때도 툴 결과의 **필드 값 그대로** 사용한다.
        - 카테고리는 `categoryDisplayName` 필드를 그대로 출력하고, 새로운 분류/병합/재분류 금지.
        - 문자열을 강조하려면 마크다운 **백틱(`) 또는 굵게(**)** 만 사용하고, 내용 자체를 바꾸지 않는다.
        
        ### Tool 함수에서 받은 데이터를 표시할 때:
        1. **절대 의역, 번역, 수정하지 않습니다**
        2. **문자 하나도 바꾸지 않고 정확히 복사합니다**
        3. 특히 고유명사(자산명, 계좌명, 종목명)는 **원문 그대로** 사용합니다
    
        ### 잘못된 예시
        - 원본: "헬리오시티 84H" → 잘못: "펄리시티 84H"
        - 원본: "삼성전자" → 잘못: "삼성 전자"
    
        ### 데이터에서 조회한 다음 단어들은 반드시 원문 그대로 사용하세요.
        - 헬리오시티 84H, 반포자이, 광진그랜드파크, 테슬라, 엔비디아 
    
        **데이터를 표시할 때는 Tool 응답에서 받은 텍스트를 ctrl+c, ctrl+v 하듯이 정확히 복사하세요.**
        
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
                .tools(financialTools)
                .stream()
                .content();
    }

    public String answer(String userQuestion) {
        var response = chatClient
                .prompt()
                .system(buildSystemPrompt())
                .user(userQuestion)
                .tools(financialTools)
                .call()
                .content();

        return response;
    }

    public ResponseEntity<?> askFinance(Integer year){

        String question = "최근 " + year + "년 간 나의 자산 현황과 최근 " + year + "년 간 소비를 바탕으로 자산 관리와 투자 방향을 제사허새요";


        String text = chatClient
                .prompt()
                .system(buildSystemPrompt())
                .user(question)
                .tools(financialTools)
                .call()
                .content();

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.AI_SUMMARY_LOAD_SUCCESS.getMessage(),
                text));
    }
}

/*

public String answer(String userQuestion) {
        var response = chatClient
                .prompt()
                .system(buildSystemPrompt())
                .user(userQuestion)
                .tools(financeTools)
                .options(ChatOptions.builder()
                        .temperature(0.3)
                        .build())
                .call()
                .content();

        return response;
    }

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