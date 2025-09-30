package com.konkuk.moneymate.ai.tools;

import com.konkuk.moneymate.activities.dto.StockHoldingDto;
import com.konkuk.moneymate.activities.entity.Asset;
import com.konkuk.moneymate.activities.entity.BankAccount;
import com.konkuk.moneymate.activities.entity.StockTransaction;
import com.konkuk.moneymate.activities.entity.Transaction;
import com.konkuk.moneymate.activities.enums.TransactionCategory;
import com.konkuk.moneymate.activities.repository.*;
import com.konkuk.moneymate.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceTools {

    private final AccountStockRepository accountStockRepository;
    private final AssetRepository assetRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
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
            description = "현재 로그인한 사용자의 모든 자산(현금/예적금/부동산/기타 등)을 조회한다.")
    public List<Asset> getAllAssets() {
        UUID userUid = currentUserUid();
        return assetRepository.findByUser_Uid(userUid);
    }

    /** 계좌 목록 조회 */
    @Tool(name = "get_bank_accounts",
            description = "현재 로그인한 사용자의 계좌 목록을 depositType(입출금/예적금/증권) 기준으로 조회한다. depositType 생략 시 전체.")
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
            description = "accountUids와 기간으로 카테고리별 지출 합계를 조회한다. start/end는 YYYY-MM 형식 허용.")
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
            description = "startDay/endDay(YYYY-MM-DD) 기간 동안 현재 로그인 사용자의 전체 계좌에 대한 카테고리별 지출 합계를 조회한다. OUTCOME/BOTH 카테고리만 포함하며 키는 displayName을 사용한다."
    )
    public Map<String, Object> getConsumptionStats(String startDay, String endDay) {
        UUID userUid = currentUserUid();

        // 날짜 파싱 및 [start, end) 구간 설정 (endDay 다음날 00:00까지)
        LocalDate startDate = LocalDate.parse(startDay);
        LocalDate endDate   = LocalDate.parse(endDay);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end   = endDate.plusDays(1).atStartOfDay();

        // 사용자 전체 계좌 UID 목록
        List<UUID> accountUids = bankAccountRepository.findByUser_Uid(userUid).stream()
                .map(BankAccount::getUid)
                .collect(Collectors.toList());

        // 카테고리 합계 맵 (OUTCOME/BOTH만 0으로 초기화)
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
                    // 카테고리 null인 경우 (DB 이상치): "기타"로 기록
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
            description = "특정 계좌(uid)와 기간(start/end: YYYY-MM)으로 증권 거래내역을 조회한다.")
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
            description = "특정 계좌(uid)와 기간(start/end: YYYY-MM)으로 일반 거래내역을 조회한다.")
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
}
