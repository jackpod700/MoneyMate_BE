package com.konkuk.moneymate.activities.service;

import static java.util.Locale.filter;

import com.konkuk.moneymate.activities.dto.AssetHistoryDto;
import com.konkuk.moneymate.activities.entity.AccountStock;
import com.konkuk.moneymate.activities.entity.Asset;
import com.konkuk.moneymate.activities.entity.BankAccount;
import com.konkuk.moneymate.activities.entity.ExchangeHistory;
import com.konkuk.moneymate.activities.entity.Stock;
import com.konkuk.moneymate.activities.entity.StockTransaction;
import com.konkuk.moneymate.activities.entity.Transaction;
import com.konkuk.moneymate.activities.repository.AccountStockRepository;
import com.konkuk.moneymate.activities.repository.AssetRepository;
import com.konkuk.moneymate.activities.repository.BankAccountRepository;
import com.konkuk.moneymate.activities.repository.ExchangeHistoryRepository;
import com.konkuk.moneymate.activities.repository.StockPriceHistoryRepository;
import com.konkuk.moneymate.activities.repository.TransactionRepository;
import com.konkuk.moneymate.activities.entity.StockPriceHistory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {

    private final AssetRepository assetRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountStockRepository accountStockRepository;
    private final StockPriceHistoryRepository stockPriceHistoryRepository;
    private final ExchangeHistoryRepository exchangeHistoryRepository;

    public StatisticService(AssetRepository assetRepository,
                            BankAccountRepository bankAccountRepository,
                            TransactionRepository transactionRepository,
                            AccountStockRepository accountStockRepository,
                            StockPriceHistoryRepository stockPriceHistoryRepository,
                            ExchangeHistoryRepository exchangeHistoryRepository
                            ) {
        this.assetRepository = assetRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.accountStockRepository = accountStockRepository;
        this.stockPriceHistoryRepository = stockPriceHistoryRepository;
        this.exchangeHistoryRepository = exchangeHistoryRepository;
    }

    private static final int monthNum = 120; //10년=120개월

    public HashMap<YearMonth, BigDecimal> getTotalAssetHistory(String userUid) {

        HashMap<YearMonth, BigDecimal> result = getAssetHistory(userUid);
        HashMap<YearMonth, BigDecimal> withdrawal = getBankAccountHistory(userUid,"입출금");
        HashMap<YearMonth, BigDecimal> deposit = getBankAccountHistory(userUid,"예적금");
        HashMap<YearMonth, BigDecimal> stock = getBankAccountHistory(userUid,"증권");
        result.replaceAll((yearMonth, currentAsset) -> {
            BigDecimal balanceToAdd = withdrawal.getOrDefault(yearMonth, BigDecimal.ZERO);
            return currentAsset.add(balanceToAdd);
        });
        result.replaceAll((yearMonth, currentAsset) -> {
            BigDecimal balanceToAdd = deposit.getOrDefault(yearMonth, BigDecimal.ZERO);
            return currentAsset.add(balanceToAdd);
        });
        result.replaceAll((yearMonth, currentAsset) -> {
            BigDecimal balanceToAdd = stock.getOrDefault(yearMonth, BigDecimal.ZERO);
            return currentAsset.add(balanceToAdd);
        });
        return result;
    }

    public HashMap<YearMonth, BigDecimal> getBankAccountHistory(String userUid, String category) {
        HashMap<YearMonth, BigDecimal> result = new HashMap<>();
        YearMonth currentMonth = YearMonth.now();
        for(int i=0;i<monthNum;i++){
            result.put(currentMonth.minusMonths(i),BigDecimal.ZERO);
        }

        List<BankAccount> bankAccounts = bankAccountRepository.findByUser_UidAndDepositType(UUID.fromString(userUid), category);
        for (BankAccount bankAccount : bankAccounts) {
            // 각 은행 계좌의 거래내역 가져오기
            List<Transaction> transactionList = transactionRepository.findByBankAccountUidAndTimeBetween(
                    bankAccount.getUid(),
                    YearMonth.now().minusMonths(monthNum).atDay(1).atStartOfDay(),
                    YearMonth.now().atEndOfMonth().atTime(23, 59, 59)
            );
            // 거래내역이 없다면 현재 잔액을 그대로 더해줌
            if (transactionList.isEmpty()) {
                Long currentBalance = bankAccount.getCurrentBalance();
                BigDecimal balanceToAdd = BigDecimal.valueOf(currentBalance);
                result.replaceAll((yearMonth, currentAsset) -> currentAsset.add(balanceToAdd));
                continue;
            }

            // 거래내역이 있다면 해당 월의 마지막 거래내역을 기준으로 afterBalance를 더해줌
            HashMap<YearMonth, BigDecimal> monthlyBalanceMap = getSingleBankAccountHistory(transactionList);
            result.replaceAll((yearMonth, currentAsset) -> {
                //    만약 해당 월의 거래내역이 없다면 기본값으로 0을 사용합니다.
                BigDecimal balanceToAdd = monthlyBalanceMap.getOrDefault(yearMonth, BigDecimal.ZERO);

                // 기존 자산 가치(currentAsset)에 조회된 잔액을 더한 값을 반환.
                return currentAsset.add(balanceToAdd);
            });

            //증권계좌의 경우 해당 달의 주식 가격 계산하여 포함
            if(category.equals("증권")){
                HashMap<YearMonth, BigDecimal> stockHistory = getStockHistory(bankAccount);
                result.replaceAll((yearMonth, currentAsset) -> {
                    //    만약 해당 월의 거래내역이 없다면 기본값으로 0을 사용합니다.
                    BigDecimal balanceToAdd = stockHistory.getOrDefault(yearMonth, BigDecimal.ZERO);

                    // 기존 자산 가치(currentAsset)에 조회된 잔액을 더한 값을 반환.
                    return currentAsset.add(balanceToAdd);
                });
            }
        }
        return result;
    }

    private HashMap<YearMonth, BigDecimal> getSingleBankAccountHistory(List<Transaction> transactions) {
        HashMap<YearMonth, BigDecimal> result = new HashMap<>();
        YearMonth currentMonth = YearMonth.now();
        if(transactions.isEmpty()){
            throw new IllegalArgumentException("No transactions found for the given bank account.");
        }

        Optional<Transaction> lastTransaction= Optional.empty();
        for (int i = 0; i < monthNum; i++) {
            YearMonth month = currentMonth.minusMonths(i);
            // 현재 달 마지막날 이전의 마지막 거래내역을 찾기
            Optional<Transaction> tempTransaction = findLastTransactionBeforeEndOfMonth(transactions, month);
            // 해당 달에 거래내역이 없으면 다음 달의 거래내역을 유지
            if(tempTransaction.isPresent()){
                lastTransaction = tempTransaction;
            }
            BigDecimal afterBalance = lastTransaction.map(Transaction::getAfterBalance)
                    .map(BigDecimal::valueOf)
                    .orElse(BigDecimal.ZERO);
            result.put(month, afterBalance);
        }
        return result;
    }

    //주어진 거래내역의 리스트에서 원하는 달을 기준으로 마지막 거래내역을 찾아서 after balance를 리턴하는 함수
    private Optional<Transaction> findLastTransactionBeforeEndOfMonth(List<Transaction> transactions, YearMonth targetMonth) {
        // 1. 시간 경계 설정: 기준달의 다음 달 1일 0시
        LocalDateTime boundaryTime = targetMonth.plusMonths(1).atDay(1).atStartOfDay();

        // 2. Stream을 사용하여 필터링 후 최신 내역 찾기
        return transactions.stream()
                // 경계 시간보다 이전에 일어난 거래만 필터링
                .filter(t -> t.getTime().isBefore(boundaryTime))
                // 필터링된 거래 중 시간이 가장 최신인 것을 찾음
                .max(Comparator.comparing(Transaction::getTime));
    }

    private HashMap<YearMonth, BigDecimal> getStockHistory(BankAccount bankAccount){
        HashMap<YearMonth, BigDecimal> result = new HashMap<>();
        YearMonth currentMonth = YearMonth.now();
        for (int i = 0; i < monthNum; i++) {
            YearMonth month = currentMonth.minusMonths(i);
            result.put(month, BigDecimal.ZERO);
        }

        // 1. 10년간의 주식 거래내역(StockTransaction)을 가져온다.
        List<StockTransaction> stockTransactions = transactionRepository.findStockTransactionByBankAccountUidAndTimeBetween(
                bankAccount.getUid(),
                YearMonth.now().minusMonths(monthNum).atDay(1).atStartOfDay(),
                YearMonth.now().atEndOfMonth().atTime(23, 59, 59)
                );
        // 2. 현재 보유중인 주식 정보(AccountStock)를 가져온다.
        List<AccountStock> stocks = accountStockRepository.findAccountStocksByBankAccount(bankAccount);
        HashMap<Stock, Integer> stockInfo = new HashMap<>();
        for(AccountStock stock:stocks){
            stockInfo.put(stock.getStock(), stock.getQuantity());
        }

        // 3. 주식 거래내역과 현재 보유중인 주식정보를 토대로 특정 달의 마지막날에 보유중인 주식을 계산한다(시간역순)
        HashMap<YearMonth, HashMap> holdingStockHistory = new HashMap<>();
        currentMonth = YearMonth.now();
        holdingStockHistory.put(currentMonth,stockInfo);
        for (int i = 1; i < monthNum; i++) {
            YearMonth targetMonth = currentMonth.minusMonths(i);
            LocalDateTime startBoundaryTime = targetMonth.atDay(1).atStartOfDay();
            LocalDateTime endBoundaryTime = targetMonth.plusMonths(1).atDay(1).atStartOfDay();

            List<StockTransaction> lists = stockTransactions.stream()
                    .filter(t->t.getTime().isAfter(startBoundaryTime))
                    .filter(t->t.getTime().isBefore(endBoundaryTime))
                    .toList();

            for (StockTransaction stockTransaction : lists){
                if(stockInfo.containsKey(stockTransaction.getStock())){
                    stockInfo.replace(stockTransaction.getStock(),
                            stockInfo.get(stockTransaction.getStock())-stockTransaction.getQuantity());
                }
                else{
                    stockInfo.put(stockTransaction.getStock(),-(stockTransaction.getQuantity()));
                }
            }
            holdingStockHistory.put(targetMonth,stockInfo);
        }

        // 4. 주식이 원화가 아니라면 해당 달의 마지막날의 환율정보를 가지고 원화로 변환한다.
        // 5. 계산한 달의 모든주식의 가격을 합쳐서 result의 totalPrice의 값에 더한다.
        currentMonth = YearMonth.now();
        for(int i=0;i<monthNum;i++){
            //각 달에 따른 계산
            YearMonth targetMonth = currentMonth.minusMonths(i);
            LocalDate boundaryTime = targetMonth.atEndOfMonth();
            HashMap<Stock, Integer> targetMonthHoldingStock = new HashMap<>(holdingStockHistory.get(targetMonth));
            List<Stock> holdingStock = new ArrayList<>(targetMonthHoldingStock.keySet());

            BigDecimal lastDayEndPrice=new BigDecimal(0);
            for(Stock stock : holdingStock){
                //각 주식에 따른 계산
                BigDecimal exchangePrice = new BigDecimal(1);
                //환율 가격 계산
                if(!stock.getCurrency().equals("KRW")){
                    exchangePrice = exchangeHistoryRepository.findByBaseCurrency(stock.getCurrency()).stream()
                            .filter(t->t.getDate().isBefore(boundaryTime))
                            .max(Comparator.comparing(ExchangeHistory::getDate))
                            .map(ExchangeHistory::getEndPrice)
                            .orElseThrow(() -> new RuntimeException("해당 조건에 맞는 환율 정보가 없습니다."));
                }
                //현재 계산중인 주식의 현재 달의 마지막 가격 가져오기
                List<StockPriceHistory> stockPriceHistory = stockPriceHistoryRepository.findByISINOrderByDateDesc(stock.getISIN());
                Optional<StockPriceHistory> lastDayStockPriceHistory = stockPriceHistory.stream()
                        // 경계 시간보다 이전에 일어난 거래만 필터링
                        .filter(t -> t.getDate().isBefore(boundaryTime))
                        // 필터링된 거래 중 시간이 가장 최신인 것을 찾음
                        .max(Comparator.comparing(StockPriceHistory::getDate));
                if(lastDayStockPriceHistory.isPresent()){
                    lastDayEndPrice=lastDayStockPriceHistory.get().getEndPrice();
                }
                //result에 더해주기
                result.replace(targetMonth,
                        result.get(targetMonth)
                                .add(lastDayEndPrice
                                        .multiply(new BigDecimal(targetMonthHoldingStock.get(stock).toString()))
                                        .multiply(exchangePrice)));
            }

        }
        return result;
    }

    public HashMap<YearMonth, BigDecimal> getAssetHistory(String userUid) {
        HashMap<YearMonth, BigDecimal> result = new HashMap<>();

        List<Asset> assets = assetRepository.findByUser_Uid(UUID.fromString(userUid));
        BigDecimal assetTotalPrice = BigDecimal.valueOf(assets.stream()
                .mapToLong(Asset::getPrice) // Asset 객체에서 price(Long)만 추출
                .sum());
        YearMonth currentMonth = YearMonth.now();
        for (int i = 0; i < monthNum; i++) {
            YearMonth month = currentMonth.minusMonths(i);
            result.put(month, assetTotalPrice);
        }

        return result;
    }
}
