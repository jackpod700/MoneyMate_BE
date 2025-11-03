package com.konkuk.moneymate.ai.tools;

import com.konkuk.moneymate.activities.dto.StockHoldingDto;
import com.konkuk.moneymate.activities.dto.news.NewsSummarizeDto;
import com.konkuk.moneymate.activities.entity.Asset;
import com.konkuk.moneymate.activities.entity.BankAccount;
import com.konkuk.moneymate.activities.entity.StockTransaction;
import com.konkuk.moneymate.activities.entity.Transaction;
import com.konkuk.moneymate.activities.entity.news.NewsSummarize;
import com.konkuk.moneymate.activities.enums.TransactionCategory;
import com.konkuk.moneymate.activities.repository.AccountStockRepository;
import com.konkuk.moneymate.activities.repository.AssetRepository;
import com.konkuk.moneymate.activities.repository.BankAccountRepository;
import com.konkuk.moneymate.activities.repository.TransactionRepository;
import com.konkuk.moneymate.activities.repository.news.NewsSummarizeRepository;
import com.konkuk.moneymate.auth.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyzerTools {

    private final AccountStockRepository accountStockRepository;
    private final AssetRepository assetRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final NewsSummarizeRepository newsSummarizeRepository;
    private final JwtService jwtService;
    private final HttpServletRequest request;

    private UUID currentUserUid() {
        String uidStr = jwtService.getUserUid(request);
        return UUID.fromString(uidStr);
    }

    /** 증권계좌 보유 종목 조회 */
    @Tool(name = "get_stock_holdings",
            description = "현재 로그인한 사용자의 증권계좌 보유 종목, 수량, 평단가, 통화 등을 조회한다.")
    public List<StockHoldingDto> getStockHoldings() {
        UUID userUid = currentUserUid();
        return accountStockRepository.findAllStockHoldings(userUid);
    }

    /** 자산 전체 조회 */
    @Tool(name = "get_all_assets",
            description = """
            현재 로그인한 사용자의 모든 자산(현금/예적금/부동산/기타 등)을 조회한다.
            
            필수 준수사항:
            - 반환된 'name' 필드는 절대 변경 금지
            - 한 글자도 바꾸지 말고 원본 그대로 사용
            - 예: "헬리오시티 84H"는 반드시 "헬리오시티 84H"로 표시
            - 의역, 번역, 수정 절대 금지
            
            반환 형식: [{assetUid, assetName, assetPrice}]
            """)
    public List<Map<String, Object>> getAllAssets() {
        UUID userUid = currentUserUid();
        List<Asset> list = assetRepository.findByUser_Uid(userUid);

        return list.stream()
                .map(a -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("assetUid", a.getUid());
                    m.put("assetName", a.getName());
                    m.put("assetPrice", a.getPrice());
                    return m;
                })
                .collect(Collectors.toList());
    }

    /** 계좌 목록 조회 */
    @Tool(name = "get_bank_accounts",
            description = "현재 로그인한 사용자의 계좌 목록을 depositType(입출금/예적금/증권) 기준으로 조회한다. " +
                    "depositType 생략 시 전체. 반환된 계좌명, 은행명 등은 원본 그대로 사용해야 한다.")
    public List<Map<String, Object>> getBankAccounts(Optional<String> depositType) {
        UUID userUid = currentUserUid();
        List<BankAccount> list = depositType
                .map(dt -> bankAccountRepository.findByUser_UidAndDepositType(userUid, dt))
                .orElseGet(() -> bankAccountRepository.findByUser_Uid(userUid));

        return list.stream().map(ba -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("uid", ba.getUid());
            m.put("bank", ba.getBank());
            m.put("name", ba.getName());
            m.put("accountNumber", ba.getAccountNumber());
            m.put("depositType", ba.getDepositType());
            m.put("balance", ba.getCurrentBalance());
            return m;
        }).collect(Collectors.toList());
    }

    /** 카테고리별 지출 합계 (월 범위) */
    @Tool(name = "get_spending_by_category",
            description = "accountUids와 기간으로 카테고리별 지출 합계를 조회한다. start/end는 YYYY-MM 형식 허용. 데이터베이스에서 가져온 필드명은 그대로 사용해야 합니다.")
    public List<Map<String, Object>> getSpendingByCategory(
            List<UUID> accountUids,
            String startYm,
            String endYm
    ) {
        YearMonth s = YearMonth.parse(startYm);
        YearMonth e = YearMonth.parse(endYm);
        LocalDateTime start = s.atDay(1).atStartOfDay();
        LocalDateTime end = e.atEndOfMonth().atTime(23, 59, 59);

        List<Object[]> rows = transactionRepository.consumptionAmountsByCategory(accountUids, start, end);
        return rows.stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            if (r[0] instanceof TransactionCategory tc) {
                m.put("category", tc.toString());
            } else {
                m.put("category", String.valueOf(r[0]));
            }
            m.put("outcomeSum", r[1]);
            return m;
        }).collect(Collectors.toList());
    }

    /** 소비 통계 (일자 범위) */
    @Tool(
            name = "get_consumption_stats",
            description = "startDay/endDay(YYYY-MM-DD) 기간 동안 현재 로그인 사용자의 전체 계좌에 대한 카테고리별 지출 합계를 조회한다. " +
                    "OUTCOME/BOTH 카테고리만 포함하며 키는 displayName을 사용한다. " +
                    "데이터베이스에서 가져온 필드명은 그대로 사용해야 합니다."
    )
    public Map<String, Object> getConsumptionStats(String startDay, String endDay) {
        UUID userUid = currentUserUid();

        LocalDate startDate = LocalDate.parse(startDay);
        LocalDate endDate   = LocalDate.parse(endDay);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end   = endDate.plusDays(1).atStartOfDay();

        List<UUID> accountUids = bankAccountRepository.findByUser_Uid(userUid).stream()
                .map(BankAccount::getUid)
                .collect(Collectors.toList());

        Map<String, Long> categoryTotals = new LinkedHashMap<>();
        for (TransactionCategory category : TransactionCategory.values()) {
            if (category.getFlow() == TransactionCategory.FlowType.OUTCOME
                    || category.getFlow() == TransactionCategory.FlowType.BOTH) {
                categoryTotals.put(category.getDisplayName(), 0L);
            }
        }

        if (!accountUids.isEmpty()) {
            List<Object[]> rows = transactionRepository.consumptionAmountsByCategory(accountUids, start, end);
            for (Object[] row : rows) {
                TransactionCategory category = (TransactionCategory) row[0];
                Long sum = ((Number) row[1]).longValue();

                if (category != null) {
                    if (category.getFlow() == TransactionCategory.FlowType.OUTCOME
                            || category.getFlow() == TransactionCategory.FlowType.BOTH) {
                        categoryTotals.put(category.getDisplayName(), sum);
                    }
                } else {
                    categoryTotals.merge("기타", sum, Long::sum);
                }
            }
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("startDate", startDate);
        resp.put("endDate", endDate);
        resp.put("categoryTotals", categoryTotals);
        return resp;
    }

    /** 증권 거래내역 조회 */
    @Tool(name = "get_stock_transactions",
            description = "특정 계좌(uid)와 기간(start/end: YYYY-MM)으로 증권 거래내역을 조회한다." +
                    "데이터베이스에서 가져온 필드명은 그대로 사용해야 합니다.")
    public List<Map<String, Object>> getStockTransactions(
            UUID accountUid,
            String startYm,
            String endYm
    ) {
        YearMonth s = YearMonth.parse(startYm);
        YearMonth e = YearMonth.parse(endYm);
        LocalDateTime start = s.atDay(1).atStartOfDay();
        LocalDateTime end = e.atEndOfMonth().atTime(23, 59, 59);

        List<StockTransaction> list = transactionRepository
                .findStockTransactionByBankAccountUidAndTimeBetween(accountUid, start, end);

        return list.stream().map(t -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("transactionNo", t.getTransactionNo());
            m.put("time", t.getTime());
            m.put("stockName", t.getStock().getName());
            m.put("ticker", t.getStock().getTicker());
            m.put("isin", t.getStock().getISIN());
            m.put("quantity", t.getQuantity());
            m.put("income", t.getIncome() != null ? t.getIncome() : 0);
            m.put("outcome", t.getOutcome() != null ? t.getOutcome() : 0);
            m.put("afterBalance", t.getAfterBalance());
            return m;
        }).collect(Collectors.toList());
    }

    /** 일반 거래내역 조회 */
    @Tool(name = "get_raw_transactions",
            description = "특정 계좌(uid)와 기간(start/end: YYYY-MM)으로 일반 거래내역을 조회한다. " +
                    "데이터베이스에서 가져온 필드명은 그대로 사용해야 합니다.")
    public List<Map<String, Object>> getRawTransactions(
            UUID accountUid,
            String startYm,
            String endYm
    ) {
        YearMonth s = YearMonth.parse(startYm);
        YearMonth e = YearMonth.parse(endYm);
        LocalDateTime start = s.atDay(1).atStartOfDay();
        LocalDateTime end = e.atEndOfMonth().atTime(23, 59, 59);

        List<Transaction> list = transactionRepository.findByBankAccountUidAndTimeBetween(accountUid, start, end);
        return list.stream().map(t -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("transactionNo", t.getTransactionNo());
            m.put("time", t.getTime());
            m.put("counterAccount", t.getCounterAccount());
            m.put("income", t.getIncome() != null ? t.getIncome() : 0);
            m.put("outcome", t.getOutcome() != null ? t.getOutcome() : 0);
            m.put("category", t.getCategory() != null ? t.getCategory().toString() : null);
            m.put("afterBalance", t.getAfterBalance());
            return m;
        }).collect(Collectors.toList());
    }

    // ==================== 종합 위험등급(상품 1~6) 판정 ====================

    @Tool(
            name = "determine_risk_grade",
            description = """
        포트폴리오의 고/중/저위험 비중과 레버리지·파생상품 보유 여부를 바탕으로 1~6등급(금융투자상품 위험등급)을 판정한다.
        입력 권장: highRiskPct, midRiskPct, lowRiskPct (합계≈100), holdingNames.
        반환: { grade(1~6), label, rationale, input:{...} }
        """
    )
    public Map<String, Object> determineRiskGrade(
            double highRiskPct,
            double midRiskPct,
            double lowRiskPct,
            List<String> holdingNames
    ) {
        boolean hasLeverageOrDerivatives = detectLeverageOrDerivatives(holdingNames);

        int grade;
        String label;
        String rationale;

        if (hasLeverageOrDerivatives || highRiskPct >= 90.0) {
            grade = 1; label = "매우높은위험";
            rationale = "레버리지/파생 추정 보유 또는 고위험자산 비중이 90% 이상";
        } else if (highRiskPct >= 80.0) {
            grade = 2; label = "높은위험";
            rationale = "고위험자산 비중이 80% 이상";
        } else if (highRiskPct >= 50.0) {
            grade = 3; label = "다소높은위험";
            rationale = "고위험자산 비중이 50% 이상 80% 미만";
        } else if (highRiskPct < 50.0 && midRiskPct >= 60.0) {
            grade = 4; label = "보통위험";
            rationale = "고위험자산 < 50%이며 중위험자산 60% 이상";
        } else if (lowRiskPct >= 80.0) {
            grade = 6; label = "매우낮은위험";
            rationale = "저위험자산 80% 이상(현금/국공채/단기MMF 중심)";
        } else if (lowRiskPct >= 60.0) {
            grade = 5; label = "낮은위험";
            rationale = "저위험자산 60% 이상";
        } else {
            if (highRiskPct >= midRiskPct && highRiskPct >= lowRiskPct) {
                grade = (highRiskPct >= 50 ? 3 : 2);
                label = (grade == 3 ? "다소높은위험" : "높은위험");
                rationale = "고위험 비중이 상대적으로 가장 큼(보정)";
            } else if (midRiskPct >= highRiskPct && midRiskPct >= lowRiskPct) {
                grade = 4; label = "보통위험";
                rationale = "중위험 비중이 상대적으로 가장 큼(보정)";
            } else {
                grade = 5; label = "낮은위험";
                rationale = "저위험 비중이 상대적으로 가장 큼(보정)";
            }
        }

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("grade", grade);
        m.put("label", label);
        m.put("rationale", rationale);
        m.put("input", Map.of(
                "highRiskPct", highRiskPct,
                "midRiskPct",  midRiskPct,
                "lowRiskPct",  lowRiskPct,
                "hasLeverageOrDerivatives", hasLeverageOrDerivatives
        ));
        return m;
    }

    private boolean detectLeverageOrDerivatives(List<String> holdingNames) {
        if (holdingNames == null) return false;
        String[] keys = {"레버리지","인버스","2x","3x","Ultra","UPro","Lev","LB","선물","옵션","파생"};
        for (String n : holdingNames) {
            if (!StringUtils.hasText(n)) continue;
            String s = n.toLowerCase(Locale.ROOT);
            for (String k : keys) {
                if (s.contains(k.toLowerCase(Locale.ROOT))) return true;
            }
        }
        return false;
    }

    // ==================== 고객투자성향(1~5) 판정 ====================

    @Tool(
            name = "determine_investor_profile",
            description = "고객 투자성향 점수(0~100)로 1~5등급 성향(공격/적극/중립/안정추구/안정)을 판정한다. 반환: {grade(1~5), label}."
    )
    public Map<String, Object> determineInvestorProfile(double score) {
        int grade;
        String label;
        if (score > 80) { grade = 1; label = "공격투자형"; }
        else if (score > 60) { grade = 2; label = "적극투자형"; }
        else if (score > 40) { grade = 3; label = "위험중립형"; }
        else if (score > 20) { grade = 4; label = "안정추구형"; }
        else { grade = 5; label = "안정형"; }
        return Map.of("grade", grade, "label", label, "score", score);
    }

    // ==================== 성향↔상품 위험등급 적합성 체크 ====================

    @Tool(
            name = "check_suitability",
            description = "투자자 성향등급(1~5)과 상품 위험등급(1~6)으로 적합성 여부를 판정한다. 반환: {allowed: boolean, rationale}."
    )
    public Map<String, Object> checkSuitability(int investorProfileGrade, int productRiskGrade) {
        boolean allowed;
        String rationale;
        switch (investorProfileGrade) {
            case 1 -> { allowed = productRiskGrade >= 1 && productRiskGrade <= 6; rationale = "공격투자형: 1~6 허용"; }
            case 2 -> { allowed = productRiskGrade >= 2 && productRiskGrade <= 6; rationale = "적극투자형: 2~6 허용(1은 부적합)"; }
            case 3 -> { allowed = productRiskGrade >= 4 && productRiskGrade <= 6; rationale = "위험중립형: 4~6 허용"; }
            case 4 -> { allowed = productRiskGrade >= 5 && productRiskGrade <= 6; rationale = "안정추구형: 5~6 허용"; }
            case 5 -> { allowed = productRiskGrade == 6; rationale = "안정형: 6만 허용"; }
            default -> { allowed = false; rationale = "알 수 없는 성향 등급"; }
        }
        return Map.of("allowed", allowed, "rationale", rationale,
                "investorProfileGrade", investorProfileGrade, "productRiskGrade", productRiskGrade);
    }

    // ==================== 개별 상품 위험등급(1~6) 간이 분류기 ====================

    @Tool(
            name = "classify_product_risk",
            description = "상품 속성으로 금융투자상품 위험등급(1~6)을 추정한다. 반환: {grade, label, rationale}."
    )
    public Map<String, Object> classifyProductRisk(
            String type,
            Optional<String> creditRating,
            Optional<Boolean> leverage,
            Optional<Boolean> principalProtected,
            Optional<String> subtype
    ) {
        String t = (type == null) ? "" : type.toUpperCase(Locale.ROOT);
        String sub = subtype.orElse("").toUpperCase(Locale.ROOT);
        String cr = creditRating.orElse("").toUpperCase(Locale.ROOT);
        boolean lev = leverage.orElse(false);
        boolean pp = principalProtected.orElse(false);

        int grade = 4; // 기본 보통위험
        String label = "보통위험";
        String why = "기본 규칙 적용";

        if (t.equals("MMF")) { grade = 6; label = "매우낮은위험"; why = "단기금융집합투자기구(MMF)"; }
        else if (t.equals("RP") || t.equals("CMA")) { grade = 5; label = "낮은위험"; why = "단기채/현금성 유사"; }
        else if (t.equals("BOND")) {
            if (sub.contains("GOV") || sub.contains("SOVEREIGN") || sub.contains("국고") || sub.contains("통안") || sub.contains("지방") || sub.contains("보증")) {
                grade = 6; label = "매우낮은위험"; why = "국고/통안/지방/정부보증 등";
            } else if (cr.startsWith("AA")) { grade = 6; label = "매우낮은위험"; why = "AA- 이상 채권"; }
            else if (cr.startsWith("A")) { grade = 5; label = "낮은위험"; why = "A 계열 채권"; }
            else if (cr.startsWith("BBB")) { grade = 4; label = "보통위험"; why = "BBB 계열 채권"; }
            else if (cr.startsWith("BB") || cr.equals("무등급")) { grade = 2; label = "높은위험"; why = "투기등급/무등급 채권"; }
            else if (cr.startsWith("B")) { grade = 1; label = "매우높은위험"; why = "B+ 이하 채권"; }
        } else if (t.equals("CP")) {
            if (cr.startsWith("A1")) { grade = 5; label = "낮은위험"; }
            else if (cr.startsWith("A2")) { grade = 5; label = "낮은위험"; }
            else if (cr.startsWith("A3")) { grade = 4; label = "보통위험"; }
            else { grade = 2; label = "높은위험"; why = "CP B 이하/무등급"; }
        } else if (t.equals("STOCK") || t.equals("ETF") || t.equals("ETN") || t.equals("REIT") || t.equals("ELW")) {
            if (lev || sub.contains("LEVERAGE") || sub.contains("INVERSE")) { grade = 1; label = "매우높은위험"; why = "레버리지/인버스 노출"; }
            else if (t.equals("ELW")) { grade = 1; label = "매우높은위험"; why = "ELW"; }
            else if (t.equals("STOCK")) {
                if (sub.contains("경고") || sub.contains("위험") || sub.contains("관리") || sub.contains("KONEX") || sub.contains("K-OTC")) {
                    grade = 1; label = "매우높은위험"; why = "투자경고/위험/관리/KONEX/K-OTC";
                } else {
                    grade = 2; label = "높은위험"; why = "일반 주식";
                }
            } else if (t.equals("ETF") || t.equals("ETN")) {
                if (sub.contains("INDEX") || sub.contains("인덱스")) { grade = 3; label = "다소높은위험"; }
                else if (sub.contains("SECTOR") || sub.contains("THEME") || sub.contains("업종") || sub.contains("테마")) { grade = 2; label = "높은위험"; }
                else if (sub.contains("채권") || sub.contains("BOND")) { grade = 4; label = "보통위험"; }
                else { grade = 3; label = "다소높은위험"; }
            } else if (t.equals("REIT")) {
                grade = 2; label = "높은위험"; why = "REITs는 고위험자산 범주";
            }
        } else if (t.equals("ELS") || t.equals("ELB") || t.equals("DLS") || t.equals("DLB")) {
            if (t.equals("ELB") || t.equals("DLB")) {
                if (pp) { grade = 5; label = "낮은위험"; why = "원금지급형 ELB/DLB"; }
                else { grade = 4; label = "보통위험"; why = "부분지급형 가정 ELB/DLB"; }
            } else {
                if (pp) { grade = 4; label = "보통위험"; why = "부분지급형(≥90%) 가정"; }
                else { grade = 2; label = "높은위험"; why = "원금비보장형 ELS/DLS"; }
            }
        }

        return Map.of("grade", grade, "label", label, "rationale", why,
                "inputs", Map.of("type", t, "creditRating", cr, "leverage", lev, "principalProtected", pp, "subtype", sub));
    }

    // ==================== 뉴스 요약 조회 (패키지/메서드 시그니처 맞춤) ====================

    /**
     * DB에 저장된 최신 뉴스 요약(경제/증시/부동산 각 1건)을 조회한다.
     * 내부적으로 newsSummarizeRepository.findFirstOfAllCategoryOrderByGeneratedTimeDesc() 사용.
     */
    @Tool(
            name = "get_latest_news_summaries",
            description = "경제(economy), 증시(stock), 부동산(realestate) 카테고리별 최신 요약을 반환한다. 반환: [{category, content, generatedTime}]."
    )
    public List<Map<String, Object>> getLatestNewsSummaries() {
        List<NewsSummarizeDto> latest = newsSummarizeRepository
                .findFirstOfAllCategoryOrderByGeneratedTimeDesc()
                .stream()
                .map(NewsSummarize::toDto)
                .toList();

        return latest.stream().map(dto -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("category", dto.getCategory());
            m.put("content", dto.getContent());
            m.put("generatedTime", dto.getGeneratedTime());
            return m;
        }).toList();
    }

    /**
     * 지정 카테고리의 최신 뉴스 요약 1건을 반환한다. (economy|stock|realestate)
     * 저장소에 개별 카테고리 조회 메서드가 없으므로,
     * findFirstOfAllCategoryOrderByGeneratedTimeDesc() 결과에서 필터링한다.
     */
    @Tool(
            name = "get_news_summary_by_category",
            description = "카테고리(economy|stock|realestate)의 최신 요약 1건을 반환한다. 반환: {category, content, generatedTime} 또는 빈 객체."
    )
    public Map<String, Object> getNewsSummaryByCategory(String category) {
        if (!StringUtils.hasText(category)) return Map.of();
        String target = category.trim().toLowerCase(Locale.ROOT);

        Optional<NewsSummarizeDto> match = newsSummarizeRepository
                .findFirstOfAllCategoryOrderByGeneratedTimeDesc()
                .stream()
                .map(NewsSummarize::toDto)
                .filter(dto -> dto.getCategory() != null && dto.getCategory().equalsIgnoreCase(target))
                .findFirst();

        if (match.isEmpty()) return Map.of();

        NewsSummarizeDto dto = match.get();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("category", dto.getCategory());
        m.put("content", dto.getContent());
        m.put("generatedTime", dto.getGeneratedTime());
        return m;
    }
}
