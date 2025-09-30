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

    /** 카테고리별 지출 합계 */
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