# Stock Package (`com.konkuk.moneymate.activities.stock`)

## Overview

The `stock` package manages stock market data, including stock master information, price history, exchange rates, and user stock transactions. It integrates with EODHD API for real-time and historical stock prices and provides APIs for portfolio management.

## Package Structure

```
stock/
├── dto/
│   ├── ExchangeHistoryId.java         # Composite key for exchange history
│   ├── StockHoldingDto.java           # Stock holding data transfer
│   ├── StockHoldingResponseDto.java   # Stock holding response
│   └── StockPriceHistoryId.java       # Composite key for price history
├── entity/
│   ├── ExchangeHistory.java           # Exchange rate history entity
│   ├── Stock.java                     # Stock master data entity
│   ├── StockPriceHistory.java         # Stock price history entity
│   └── StockTransaction.java          # Stock transaction entity
├── repository/
│   ├── AccountStockRepository.java    # Account stock data access
│   ├── ExchangeHistoryRepository.java # Exchange history data access
│   ├── StockPriceHistoryRepository.java # Price history data access
│   └── StockRepository.java           # Stock master data access
├── service/
│   └── StockLastdayFetchService.java  # Stock price update service
└── StockPriceApiClient.java           # EODHD API client
```

## Core Components

### 1. StockPriceApiClient

**Purpose**: Client for EODHD (End of Day Historical Data) API

**API Base URL**: `https://eodhd.com/api/`

**API Token**: Configured in `application.properties`

**Key Methods**:

##### `getRealTimePrice(String ticker, String market)`
**Description**: Get 15-minute delayed real-time price  
**Endpoint**: `/real-time/{ticker}.{market}`  
**Example**: `getRealTimePrice("AAPL", "US")`  
**Returns**: Real-time stock data

##### `getBulkLastDay(String market)`
**Description**: Get end-of-day prices for all stocks in market  
**Endpoint**: `/eod-bulk-last-day/{market}`  
**Example**: `getBulkLastDay("US")`  
**Returns**: List of all stock prices (previous business day)

##### `getHistoricalPrices(String ticker, String market, LocalDate from, LocalDate to)`
**Description**: Get historical price data  
**Endpoint**: `/eod/{ticker}.{market}`  
**Returns**: Historical price list

##### `getExchangeRate(String fromCurrency, String toCurrency)`
**Description**: Get currency exchange rate  
**Endpoint**: `/real-time/{from}{to}`  
**Example**: `getExchangeRate("USD", "KRW")`  
**Returns**: Exchange rate

**Error Handling**:
- 401: Invalid API token
- 404: Stock not found
- 429: Rate limit exceeded
- 500: API server error

---

### 2. StockLastdayFetchService

**Purpose**: Scheduled service to fetch and update stock prices

**Schedule**: Runs via Quartz scheduler (see `QuartzConfig`)

**Key Methods**:

##### `fetchAndUpdatePrices()`
**Description**: Main method to fetch latest prices

**Process**:
```java
1. Fetch bulk prices from EODHD
2. Parse JSON response
3. Update database:
   - Update Stock.currentPrice
   - Insert StockPriceHistory record
4. Update exchange rates
5. Log update statistics
```

**Markets Supported**:
- `US` - US stocks (NYSE, NASDAQ)
- `KO` - Korean stocks (KRX)
- `KRX` - Korean market alternative code

**Scheduled Execution**:
```java
// Configured in QuartzConfig
// Runs daily after market close
// US: 4:00 PM EST → 5:00 AM KST (next day)
// KR: 3:30 PM KST
```

##### `updateExchangeRates()`
**Description**: Update currency exchange rates

**Currencies**:
- USD/KRW
- EUR/KRW
- JPY/KRW
- CNY/KRW

---

### 3. Entities

#### Stock

**Table**: `stock`

**Purpose**: Stock master data

**Key Fields**:
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String ticker;           // Stock symbol (e.g., "AAPL")
private String stockName;        // Company name
private String market;           // Market code (US, KO)
private String exchange;         // Exchange (NYSE, NASDAQ, KRX)
private String sector;           // Industry sector
private BigDecimal currentPrice; // Latest price
private String currency;         // Currency code (USD, KRW)
private LocalDateTime updatedAt; // Last price update
```

**Indexes**:
- Unique index on `(ticker, market)`
- Index on `exchange`
- Index on `sector`

#### StockPriceHistory

**Table**: `stock_price_history`

**Purpose**: Historical stock prices

**Composite Key**: `StockPriceHistoryId`

**Key Fields**:
```java
@EmbeddedId
private StockPriceHistoryId id;  // (stockId, priceDate)

@ManyToOne
@JoinColumn(name = "stock_id")
private Stock stock;

private LocalDate priceDate;
private BigDecimal openPrice;
private BigDecimal highPrice;
private BigDecimal lowPrice;
private BigDecimal closePrice;
private Long volume;
private BigDecimal adjustedClose;  // Split/dividend adjusted
```

**StockPriceHistoryId** (Composite Key):
```java
@Embeddable
public class StockPriceHistoryId implements Serializable {
    private Long stockId;
    private LocalDate priceDate;
}
```

#### ExchangeHistory

**Table**: `exchange_history`

**Purpose**: Currency exchange rate history

**Composite Key**: `ExchangeHistoryId`

**Key Fields**:
```java
@EmbeddedId
private ExchangeHistoryId id;  // (fromCurrency, toCurrency, exchangeDate)

private String fromCurrency;   // USD, EUR, JPY, etc.
private String toCurrency;     // KRW
private LocalDate exchangeDate;
private BigDecimal exchangeRate;
```

**ExchangeHistoryId** (Composite Key):
```java
@Embeddable
public class ExchangeHistoryId implements Serializable {
    private String fromCurrency;
    private String toCurrency;
    private LocalDate exchangeDate;
}
```

#### StockTransaction

**Table**: `stock_transaction`

**Purpose**: User stock buy/sell transactions

**Key Fields**:
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@ManyToOne
@JoinColumn(name = "user_id")
private User user;

@ManyToOne
@JoinColumn(name = "stock_id")
private Stock stock;

private String transactionType;  // "BUY" or "SELL"
private Integer quantity;
private BigDecimal pricePerShare;
private BigDecimal totalAmount;
private LocalDateTime transactionDate;
```

---

### 4. Repositories

#### StockRepository

**Custom Queries**:
```java
Optional<Stock> findByTickerAndMarket(String ticker, String market);
List<Stock> findByExchange(String exchange);
List<Stock> findBySector(String sector);
List<Stock> findByStockNameContaining(String keyword);
```

#### StockPriceHistoryRepository

**Custom Queries**:
```java
List<StockPriceHistory> findByStockIdAndPriceDateBetween(
    Long stockId, 
    LocalDate startDate, 
    LocalDate endDate
);

Optional<StockPriceHistory> findByStockIdAndPriceDate(
    Long stockId, 
    LocalDate date
);
```

#### ExchangeHistoryRepository

**Custom Queries**:
```java
Optional<ExchangeHistory> findByFromCurrencyAndToCurrencyAndExchangeDate(
    String from, 
    String to, 
    LocalDate date
);

List<ExchangeHistory> findByFromCurrencyAndToCurrencyAndExchangeDateBetween(
    String from, 
    String to, 
    LocalDate start, 
    LocalDate end
);
```

#### AccountStockRepository

**Purpose**: Repository for AccountStock entity (from bankaccount package)

**Custom Queries**:
```java
List<AccountStock> findByBankAccountUserId(String userId);
List<AccountStock> findByBankAccountId(Long accountId);
```

**Note**: AccountStock entity is defined in `activities.bankaccount.entity` package

---

### 5. DTOs

#### StockHoldingDto
```java
String ticker;
String stockName;
String market;
Integer quantity;
BigDecimal averagePrice;
BigDecimal currentPrice;
BigDecimal totalValue;
BigDecimal profitLoss;
Double profitLossPercent;
```

#### StockHoldingResponseDto
```java
String userId;
BigDecimal totalStockValue;
BigDecimal totalInvestment;
BigDecimal totalProfitLoss;
Double totalReturnPercent;
List<StockHoldingDto> holdings;
```

---

## Business Logic

### Price Update Workflow
1. **Fetch Bulk Data**: Get all prices from EODHD
2. **Parse Response**: Extract price data
3. **Match Stocks**: Find stocks in database
4. **Update Current Price**: Update `Stock.currentPrice`
5. **Insert History**: Add `StockPriceHistory` record
6. **Update Exchange Rates**: Fetch and store rates
7. **Log Results**: Record update statistics

### Portfolio Valuation
```java
public BigDecimal calculatePortfolioValue(String userId) {
    // Get all user's stock holdings
    List<AccountStock> holdings = accountStockRepository
        .findByBankAccountUserId(userId);
    
    BigDecimal totalValue = BigDecimal.ZERO;
    
    for (AccountStock holding : holdings) {
        Stock stock = holding.getStock();
        BigDecimal value = stock.getCurrentPrice()
            .multiply(new BigDecimal(holding.getQuantity()));
        
        // Convert to KRW if needed
        if (!"KRW".equals(stock.getCurrency())) {
            ExchangeRate rate = getExchangeRate(
                stock.getCurrency(), 
                "KRW"
            );
            value = value.multiply(rate);
        }
        
        totalValue = totalValue.add(value);
    }
    
    return totalValue;
}
```

### Profit/Loss Calculation
```java
public BigDecimal calculateProfitLoss(AccountStock holding) {
    Stock stock = holding.getStock();
    
    BigDecimal currentValue = stock.getCurrentPrice()
        .multiply(new BigDecimal(holding.getQuantity()));
    
    BigDecimal investedValue = holding.getAveragePrice()
        .multiply(new BigDecimal(holding.getQuantity()));
    
    return currentValue.subtract(investedValue);
}
```

---

## Scheduled Jobs

### Stock Price Update Job

**Trigger**: Configured in `QuartzConfig`

**Schedule**:
- Daily after market close
- US Market: 4:00 PM EST (5:00 AM KST next day)
- Korean Market: 3:30 PM KST

**Job Class**: `StockLastdayFetchJob`

**Configuration**:
```java
JobDetail jobDetail = JobBuilder
    .newJob(StockLastdayFetchJob.class)
    .withIdentity("stockLastdayFetchJob")
    .storeDurably()
    .build();

Trigger trigger = TriggerBuilder
    .newTrigger()
    .withIdentity("stockLastdayFetchTrigger")
    .withSchedule(
        CronScheduleBuilder.cronSchedule("0 0 5 * * ?")  // Daily at 5 AM KST
    )
    .build();
```

---

## Dependencies

### External
- Spring Data JPA
- Spring Scheduling
- Quartz Scheduler
- OkHttp (HTTP client)
- Gson (JSON parsing)

### Internal
- `activities.bankaccount.entity.AccountStock` - User stock holdings
- `activities.user.entity.User` - User entity
- `common.scheduling.QuartzConfig` - Scheduler configuration

---

## API Integration

### EODHD API

**Website**: https://eodhd.com

**Authentication**: API token in query string

**Endpoints Used**:
- `/real-time/{ticker}.{market}` - Real-time prices (15-min delay)
- `/eod-bulk-last-day/{market}` - Bulk end-of-day prices
- `/eod/{ticker}.{market}` - Historical prices
- `/real-time/{from}{to}` - Exchange rates

**Rate Limits**: Depends on subscription plan

**Response Format**: JSON

**Example Response**:
```json
{
  "code": "AAPL.US",
  "timestamp": 1699564800,
  "gmtoffset": 0,
  "open": 180.00,
  "high": 182.50,
  "low": 179.50,
  "close": 181.75,
  "volume": 50123456,
  "previousClose": 180.25,
  "change": 1.50,
  "change_p": 0.83
}
```

---

## Performance Considerations

### Database Optimization
- Index on `(ticker, market)` for fast lookups
- Index on `priceDate` for historical queries
- Composite keys for price/exchange history (avoid duplicates)

### Caching
- Cache current prices (5-minute TTL)
- Cache exchange rates (1-hour TTL)
- Cache stock master data (1-day TTL)

### Batch Processing
- Bulk insert price history records
- Batch update current prices
- Use transactions for consistency

---

## Error Handling

### API Errors
- **Retry Logic**: Retry failed API calls (max 3 attempts)
- **Fallback**: Use cached prices if API fails
- **Logging**: Log all API errors with details
- **Alerting**: Send alert if update fails multiple times

### Data Validation
- Validate price ranges (no negative prices)
- Validate volume (non-negative)
- Check for missing data
- Handle split/dividend adjustments

---

## Best Practices

1. **Price Updates**: Run after market close for accurate data
2. **Exchange Rates**: Update daily or use real-time rates
3. **Historical Data**: Store for at least 1 year
4. **Error Handling**: Gracefully handle API failures
5. **Monitoring**: Monitor update success rate and latency

---

## Future Enhancements

- [ ] Support more markets (Europe, Asia)
- [ ] Add intraday price updates
- [ ] Implement real-time WebSocket prices
- [ ] Add stock split/dividend handling
- [ ] Support crypto currencies
- [ ] Add technical indicators (MA, RSI, etc.)
- [ ] Implement price alerts
- [ ] Add stock screening features

---

**Package Owner**: Stock Team  
**Last Updated**: November 2024  
**Related Documentation**:
- [Parent: activities/README.md](../README.md)
- [Bank Account Package: ../bankaccount/CLAUDE.md](../bankaccount/CLAUDE.md)
- [Scheduling: ../../common/scheduling/CLAUDE.md](../../common/scheduling/CLAUDE.md)
- [EODHD API Documentation](https://eodhd.com/financial-apis/)
