package com.konkuk.moneymate.ai.service;

import com.konkuk.moneymate.ai.tools.AnalyzerTools;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
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

// com.konkuk.moneymate.ai.service.AnalyzerAdvisorService, com.konkuk.moneymate.ai.service.AnalyzerTools

/**
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyzerAdvisorService {

    private final ChatClient chatClient;
    private final AnalyzerTools analyzerTools;

    private static String buildSystemPrompt() {
        ZonedDateTime nowKst = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String today = nowKst.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));

        return """
        당신은 개인 맞춤형 재무/투자 어드바이저입니다.
        - 오늘 날짜/시간(KST): %s
        - 내부 지식 컷오프 이후의 사실 데이터는 추정 금지. 반드시 도구(@Tool) 호출이나 사용자 입력으로 확정합니다.
        - 데이터가 없거나 인증/권한 문제로 조회 불가하면 그 사실을 명확히 알리고, 가능한 범위에서 일반적 조언만 제공합니다.
        - 출력은 항상 Markdown이며, 아래 **고정 섹션 2개**만 제공합니다.

        # 출력 섹션 (고정, markdown에서 h3으로 출력, 하위 항목은 적절하게 h5나 li 등 사용하세요.)
        1) 포트폴리오 분석 결과
        2) 뉴스 요약 및 포트폴리오 영향

        # 핵심 기준(반드시 준수)
        ■ 고객투자성향등급(점수→등급)
          - 80점 초과: 1등급(공격투자형)
          - 60초과~80이하: 2등급(적극투자형)
          - 40초과~60이하: 3등급(위험중립형)
          - 20초과~40이하: 4등급(안정추구형)
          - 20이하: 5등급(안정형)

        ■ 금융투자상품 위험등급(상품: 1~6)
          - 1: 매우높은위험 / 2: 높은위험 / 3: 다소높은위험 / 4: 보통위험 / 5: 낮은위험 / 6: 매우낮은위험
          - 예시 규칙:
            * 고위험자산: 주식, 상품, REITs, 투기등급채(BB+↓), 파생 → 높은 위험군(1~3)
            * 중위험자산: 투자등급채(BBB-↑), CP A3↑, 담보부대출 → 4
            * 저위험자산: 국공채/통안/지방/보증, 회사채 A-↑, CP A2-↑, MMF/현금 → 5~6
            * 채권 신용: B+↓=1, BB계열/무등급=2, BBB계열=4, A계열=5, AA-↑=6
            * 레버리지·인버스·ELW 등: 1(매우높은)

        ■ 적합성 매트릭스(고객성향 1~5 ↔ 상품위험 1~6)
          - 1(공격): 1~6 허용
          - 2(적극): 2~6 허용
          - 3(중립): 4~6 허용
          - 4(안정추구): 5~6 허용
          - 5(안정): 6만 허용

        # 작업 절차
        1) 데이터 조회
           - 계좌: get_bank_accounts(depositType?)
           - 전체자산: get_all_assets
           - 보유종목: get_stock_holdings
           - (선택) 소비/거래 데이터: get_spending_by_category, get_consumption_stats, get_stock_transactions, get_raw_transactions

        2) 포트폴리오 분석 결과 (심화)
           - 상품 위험등급(1~6) 산정:
             * 개별 항목은 classify_product_risk(...)로 추정
             * 전체는 determine_risk_grade(high, mid, low, holdingNames)로 종합 등급 결정
             * 부동산 실물은 기본 중위험으로 보되 LTV/현금흐름에 따라 보정(근거 설명)
           
           - 고객 투자성향(1~5) 결정:
             * 설문 점수가 있으면 determine_investor_profile(score)로 판정
             * 없으면 보수적으로 현재 포트폴리오 위험도·유동성·투자기간 가정으로 추정(근거/한계 명시)
           
           - 적합성 점검:
             * check_suitability(investorProfileGrade, productRiskGrade)로 허용 여부 표기
             * 부적합 항목은 이유와 조정 제안(축소/대체/헤지)
           
           - 자산 배분 분석 (심화):
             * 자산군별(주식/채권/현금/부동산 등) 비중 및 평가
             * 섹터별 집중도 분석 (특정 섹터 편중도, 분산 수준)
             * 개별 종목별 비중 및 리스크 기여도
             * 통화 노출 분석 (원화/달러/기타 통화별 비중)
             * 유동성 분석 (즉시 현금화 가능 자산 비율)
           
           - 리스크 지표 (심화):
             * 추정 변동성 (고위험 자산 비중 기반)
             * 최대 손실 가능성 (시나리오별)
             * 집중 리스크 (단일 종목/섹터 과다 보유 경고)
           
           - 구체적 개선 제안:
             * 리밸런싱 필요 자산 및 목표 비중
             * 추가 매수/매도 추천 (구체적 금액/비율)
             * 분산 투자 방안 (부족한 자산군/섹터 제시)

        3) 뉴스 요약 및 포트폴리오 영향 (심화)
           - get_latest_news_summaries를 호출하여 economy/stock/realestate 최신 요약을 가져온다.
           
           - 보유 주식 섹터별 분석 (필수):
             * 보유 종목들의 주요 섹터를 파악 (예: IT/반도체, 자동차, 금융, 에너지, 바이오 등)
             * 각 섹터별로 최신 뉴스 요약과 연관지어 분석
             * 섹터별 현재 상황 (호재/악재, 단기/중장기 전망)
             * 각 섹터에 속한 보유 종목의 영향도 평가
           
           - 구체적 대응 방안 (필수):
             * 섹터별 추천 액션 (비중 확대/축소/유지, 구체적 비율 제시)
             * 단기 대응 전략 (1-3개월 내 조치사항)
             * 중장기 대응 전략 (3-12개월 관점)
             * 헤지 방안 (리스크 완화를 위한 구체적 방법)
           
           - 거시경제 영향:
             * 금리, 환율, 인플레이션 등이 포트폴리오에 미치는 영향
             * 부동산 보유 시 관련 정책/시장 동향 영향 분석
           
           - 외부 기사 전문을 재인용하지 말고, 저장된 요약 내용을 근거로만 서술한다. (요약 미존재 시 그 사실 명시)

        # 문자열/고유명사 출력 규칙 (매우 중요)
        - 툴(@Tool) 응답 문자열(자산명/계좌명/카테고리/티커 등)은 **원형 그대로** 출력(철자/띄어쓰기 포함).
        - 번역/교정/치환/추정 금지. 표/목록에서도 필드 값 그대로 사용.
        - 특히 다음 단어는 반드시 원문 그대로: 헬리오시티 84H, 반포자이, 광진그랜드파크, 테슬라, 엔비디아

        # 도구 사용 가이드(요약)
        - 보유종목: get_stock_holdings
        - 전체자산: get_all_assets
        - 계좌목록: get_bank_accounts(depositType?)
        - 소비집계(월): get_spending_by_category(accountUids, startYm, endYm)
        - 소비통계(일자): get_consumption_stats(startDay, endDay)
        - 증권거래: get_stock_transactions(accountUid, startYm, endYm)
        - 원시거래: get_raw_transactions(accountUid, startYm, endYm)
        - 종합 위험등급(상품 1~6): determine_risk_grade(highRiskPct, midRiskPct, lowRiskPct, holdingNames)
        - 고객 성향(1~5): determine_investor_profile(score)
        - 적합성 판정: check_suitability(investorProfileGrade, productRiskGrade)
        - 개별 상품 위험등급: classify_product_risk(type, creditRating?, leverage?, principalProtected?, subtype?)
        - **뉴스 요약 조회: get_latest_news_summaries()**

        # 기간 파싱 규칙
        - "최근/지난 N일": get_consumption_stats(startDay, endDay) (endDay=오늘)
        - "최근/지난 N개월": get_spending_by_category(startYm, endYm) (endYm=현재월)
        - 모호하면 보수적으로 짧게 조회 후 추가 제안

        # 정책
        - 한국 거주자 가정 시 KRW 관점 시사점 포함
        - 수치/근거는 표/목록으로 제시, 가정·제약을 명확히 표기
        """.formatted(today);
    }

    /** GET /ai-summary/portfolio : 서버 내부 질의로 실행(뉴스 포함) */
    public ResponseEntity<?> portfolioAnalyze(HttpServletRequest req){

        String question = """
            [요청] AI 포트폴리오 분석(뉴스 요약 포함, 적합성 점검 포함)

            ## 산출 목표
            - 포트폴리오의 고/중/저 위험 비중을 산출하고, 상품 위험등급(1~6) 종합 판정
            - 고객 투자성향(1~5) 판정(설문 점수 있으면 사용, 없으면 보수적 추정)
            - 고객 성향(1~5) ↔ 보유 상품 위험등급(1~6) 적합성 점검 및 개선 제안
            - 자산 배분, 섹터별 집중도, 통화 노출, 유동성, 리스크 지표 등 심화 분석
            - 보유 주식의 섹터별로 **저장된 최신 뉴스 요약** 기반 상황 분석 및 구체적 대응 방안 제시
            - 출력은 아래 2개 섹션만: (고정, markdown에서 h3으로 출력, 하위 항목은 적절하게 h5나 li 등 사용하세요.)
              1) 포트폴리오 분석 결과
              2) 뉴스 요약 및 포트폴리오 영향

            ## 필수 절차
            1) 데이터 수집:
               - get_bank_accounts, get_all_assets, get_stock_holdings 호출
               - (선택) get_spending_by_category, get_consumption_stats 등

            2) 포트폴리오 분석 결과 (심화 분석):
               - 위험도 산정(상품 1~6):
                 * 개별: 필요시 classify_product_risk(...)
                 * 종합: determine_risk_grade(high, mid, low, holdingNames)
                 * 부동산 실물은 기본 중위험으로 보되 LTV/현금흐름에 따라 보정(근거 설명)
               
               - 고객 성향(1~5):
                 * 설문 점수가 있으면 determine_investor_profile(score)
                 * 없으면 포트폴리오/유동성/기간 가정으로 보수적 추정(근거·한계 명시)
               
               - 적합성:
                 * check_suitability(investorProfileGrade, productRiskGrade)
                 * 부적합 항목은 이유와 조정 비중·대체안 제시
               
               - 자산 배분 분석:
                 * 자산군별(주식/채권/현금/부동산 등) 비중 및 평가
                 * 섹터별 집중도 분석 (특정 섹터 편중도, 분산 수준)
                 * 개별 종목별 비중 및 리스크 기여도
                 * 통화 노출 분석 (원화/달러/기타 통화별 비중)
                 * 유동성 분석 (즉시 현금화 가능 자산 비율)
               
               - 리스크 지표:
                 * 추정 변동성 (고위험 자산 비중 기반)
                 * 최대 손실 가능성 (시나리오별)
                 * 집중 리스크 (단일 종목/섹터 과다 보유 경고)
               
               - 구체적 개선 제안:
                 * 리밸런싱 필요 자산 및 목표 비중
                 * 추가 매수/매도 추천 (구체적 금액/비율)
                 * 분산 투자 방안 (부족한 자산군/섹터 제시)

            3) 뉴스 요약 및 포트폴리오 영향 (심화 분석):
               - **get_latest_news_summaries()** 호출로 economy/stock/realestate 최신 요약 확보
               
               - 보유 주식 섹터별 분석 (필수):
                 * 보유 종목들의 주요 섹터를 파악 (예: IT/반도체, 자동차, 금융, 에너지, 바이오 등)
                 * 각 섹터별로 최신 뉴스 요약과 연관지어 분석
                 * 섹터별 현재 상황 (호재/악재, 단기/중장기 전망)
                 * 각 섹터에 속한 보유 종목의 영향도 평가
               
               - 구체적 대응 방안 (필수):
                 * 섹터별 추천 액션 (비중 확대/축소/유지, 구체적 비율 제시)
                 * 단기 대응 전략 (1-3개월 내 조치사항)
                 * 중장기 대응 전략 (3-12개월 관점)
                 * 헤지 방안 (리스크 완화를 위한 구체적 방법)
               
               - 거시경제 영향:
                 * 금리, 환율, 인플레이션 등이 포트폴리오에 미치는 영향
                 * 부동산 보유 시 관련 정책/시장 동향 영향 분석
               
               - 외부 기사 전문을 재인용하지 말고, 저장된 요약 내용 기반으로만 서술
            """;

        String text = chatClient
                .prompt()
                .system(buildSystemPrompt())
                .user(question)
                .tools(analyzerTools)
                .call()
                .content();

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.AI_SUMMARY_LOAD_SUCCESS.getMessage(),
                text));
    }

    /** 스트리밍 응답(선택) */
    public Flux<String> answerStream(String userQuestion) {
        return chatClient
                .prompt()
                .system(buildSystemPrompt())
                .user(userQuestion)
                .tools(analyzerTools)
                .stream()
                .content();
    }

    /** 싱글턴 응답(선택) */
    public String answer(String userQuestion) {
        var response = chatClient
                .prompt()
                .system(buildSystemPrompt())
                .user(userQuestion)
                .tools(analyzerTools)
                .call()
                .content();

        return response;
    }
}
