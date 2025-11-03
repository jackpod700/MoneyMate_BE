# Templates Package (`com.konkuk.moneymate.common.templates`)

## Overview

The `templates` package contains test pages, API proxy controllers for external stock market data, scheduled tasks for market data refresh, and AI agent template pages. This package serves as a utility layer for development, testing, and external API integration.

**Note**: This package was previously located at `auth.templates` and has been moved to `common.templates` to better reflect its cross-cutting concerns.

## Package Structure

```
common/templates/
├── TestStockPage.java                 # Stock price inquiry test page
├── NaverStockProxyController.java     # Naver Stock API proxy
├── EodhdProxyController.java          # EODHD Stock API proxy
├── MarketValueRankingRefresher.java   # Scheduled market ranking refresh
└── $page$ai/                          # AI Agent test pages
    ├── controller/
    │   ├── TemplatesPageController.java    # AI agent page controller
    │   └── TemplatesRestController.java    # AI agent API endpoints
    └── service/
        ├── AgentTemplateService1.java      # AI service v1
        └── AgentTemplateService2.java      # AI service v2
```

## Test Pages

### 1. TestStockPage

**Purpose**: Test page controller for stock price inquiry functionality

**Type**: `@Controller` (returns view names)

**Package**: `com.konkuk.moneymate.common.templates`

**Endpoints**:

#### `GET /test/page/stock`
**Description**: Display stock price inquiry test page  
**View**: `test-stockpage.html` (Thymeleaf template)  
**Public Access**: Yes (defined in PERMIT_ALL_PATTERNS)

**Template Location**: `src/main/resources/templates/test-stockpage.html`

**Features**:
- Real-time stock price lookup (Naver API)
- Delayed stock prices (EODHD API)
- Market index display
- Bulk stock data view

#### `GET /proxy/naver/stock`
**Description**: Legacy proxy endpoint for Naver stock API  
**Query Params**: `ticker` (stock symbol)  
**Response**: JSON stock data from Naver

**Example**:
```
GET /proxy/naver/stock?ticker=005930
Response: { "stockName": "삼성전자", ... }
```

**Note**: This is a legacy endpoint. New code should use `NaverStockProxyController`.

---

## Stock API Proxies

### 2. NaverStockProxyController

**Purpose**: Proxy controller for Naver Stock API to bypass CORS restrictions

**Type**: `@RestController`

**Package**: `com.konkuk.moneymate.common.templates`

**Base URL**: `/api/proxy/naver-stock`

**Why Proxy?**:
- Naver API doesn't allow direct browser calls (CORS)
- API key hiding (if any)
- Rate limiting control
- Response caching potential

#### Endpoints:

##### `GET /api/proxy/naver-stock/realtime`
**Description**: Get real-time stock price

**Query Params**:
- `ticker` - Stock symbol (e.g., "005930", "AAPL")
- `region` - Market region ("KR", "US")
- `exchange` - Exchange code ("NYSE", "NASDAQ", "KRX")

**Process**:
```java
if (region == "KR") {
    fetch("https://m.stock.naver.com/api/stock/{ticker}/basic")
} else {
    try: fetch("https://api.stock.naver.com/stock/{ticker}.{exchange}/basic")
    fallback: fetch("https://api.stock.naver.com/stock/{ticker}/basic")
}
```

**Response**: JSON stock data
```json
{
  "stockName": "Apple Inc.",
  "closePrice": "150.00",
  "compareToPreviousClosePrice": "+2.50",
  ...
}
```

##### `GET /api/proxy/naver-stock/index`
**Description**: Get market index summary

**Query Params**:
- `nation` - "KOR", "USA", or "MAJOR"

**URLs**:
- KOR: `https://api.stock.naver.com/index/nation/KOR`
- USA: `https://api.stock.naver.com/index/nation/USA`
- MAJOR: `https://api.stock.naver.com/index/major`

**Response**: Market indices (KOSPI, NASDAQ, S&P 500, etc.)

##### `GET /api/proxy/naver-stock/domestic`
**Description**: Get Korean stock listings

**Query Params**:
- `type` - "market", "up", "down", "search", "industry", "theme"
- `page` - Page number
- `pageSize` - Results per page

**Types**:
- `market` - By market cap (시가총액)
- `up` - Rising stocks
- `down` - Falling stocks
- `search` - Popular search
- `industry` - By industry
- `theme` - By theme

**Example**:
```
GET /api/proxy/naver-stock/domestic?type=market&page=1&pageSize=20
```

##### `GET /api/proxy/naver-stock/ex`
**Description**: Get foreign stock listings

**Query Params**:
- `name` - Exchange: "NYSE", "NASDAQ", "AMEX"
- `type` - "market", "marketValue", "up", "down", "dividend"
- `page` - Page number
- `pageSize` - Results per page

**Example**:
```
GET /api/proxy/naver-stock/ex?name=NASDAQ&type=marketValue&page=1&pageSize=20
```

##### `GET /api/proxy/naver-stock/exchange`
**Description**: Get currency exchange rates

**URL**: `https://m.stock.naver.com/front-api/marketIndex/exchange/new`

**Response**: USD/KRW, EUR/KRW, JPY/KRW, etc.

#### Helper Method: `fetch(HttpClient client, String url)`

**Purpose**: Unified HTTP GET with error handling

**Headers**: 
- `User-Agent: Mozilla/5.0` (required by some APIs)

**Error Handling**:
- `IOException` → 502 Bad Gateway
- `InterruptedException` → 502 Bad Gateway + restore interrupt

**Returns**: `ResponseEntity<String>` with status code and body

---

### 3. EodhdProxyController

**Purpose**: Proxy controller for EODHD (End of Day Historical Data) API

**Type**: `@RestController`

**Package**: `com.konkuk.moneymate.common.templates`

**Base URL**: `/api/proxy/eodhd`

**API Token**: `687f9cc717eea0.26361602` (hardcoded)

#### Endpoints:

##### `GET /api/proxy/eodhd/realtime/15min`
**Description**: Get 15-minute delayed real-time stock price

**Query Params**:
- `ticker` - Stock symbol
- `market` - Market code (e.g., "US", "KO")

**EODHD URL**:
```
https://eodhd.com/api/real-time/{ticker}.{market}?api_token={token}&fmt=json
```

**Example**:
```
GET /api/proxy/eodhd/realtime/15min?ticker=AAPL&market=US
```

**Response**: Real-time stock data (15-minute delay)

##### `GET /api/proxy/eodhd/bulk`
**Description**: Get end-of-day prices for all stocks in a market

**Query Params**:
- `market` - Market code (e.g., "US", "KO", "KRX")

**EODHD URL**:
```
https://eodhd.com/api/eod-bulk-last-day/{market}?api_token={token}&fmt=json
```

**Response**: Array of stock data (previous business day)
```json
[
  {
    "code": "AAPL.US",
    "date": "2024-11-02",
    "close": 150.00,
    ...
  },
  ...
]
```

**Use Case**: 
- Daily portfolio valuation
- Historical price storage
- Market-wide analysis

#### Helper Method: `fetch(String url)`

**Purpose**: HTTP GET with error handling

**Similar to**: `NaverStockProxyController.fetch()`

---

### 4. MarketValueRankingRefresher

**Purpose**: Scheduled task to refresh top 100 stocks by market cap in Redis

**Type**: `@Component` (currently disabled with comment)

**Package**: `com.konkuk.moneymate.common.templates`

**Status**: Commented out (`// @Component`) to avoid slowing server startup

**Storage**: Redis Sorted Sets + Hashes

**Supported Exchanges**:
- Foreign: NASDAQ, NYSE, AMEX
- Domestic: KOSPI, KOSDAQ

#### Redis Data Structure:

**Sorted Set** (for ranking lookup):
```
Key: stocks:{exchange}    (e.g., stocks:NASDAQ)
Members: reutersCode      (e.g., NVDA.O)
Scores: rank              (1-100)

ZADD stocks:NASDAQ 1 "NVDA.O"
ZADD stocks:NASDAQ 2 "AAPL.O"
...
```

**Hash** (for detailed data):
```
Key: stockRank:{exchange}:{reutersCode}  (e.g., stockRank:NASDAQ:NVDA.O)
Fields:
  - rank: "1"
  - exchange: "NASDAQ"
  - reutersCode: "NVDA.O"
  - marketValue: "1500000000000"

HSET stockRank:NASDAQ:NVDA.O rank "1"
HSET stockRank:NASDAQ:NVDA.O exchange "NASDAQ"
HSET stockRank:NASDAQ:NVDA.O reutersCode "NVDA.O"
HSET stockRank:NASDAQ:NVDA.O marketValue "1500000000000"
```

#### Scheduled Task:

##### `@Scheduled(cron = "0 */30 * * * *")`
**Description**: Refresh all exchanges every 30 minutes

**Process**:
```java
1. Loop through exchanges: NASDAQ, NYSE, AMEX, KOSPI, KOSDAQ
2. For each exchange:
   a. Delete old sorted set
   b. Fetch top 100 stocks (paginated, 20 per page)
   c. Store in sorted set (rank as score)
   d. Store details in hash
```

#### Methods:

##### `refreshExchange(String exchange)`
**Purpose**: Refresh single exchange ranking

**Process**:
1. Clear old data: `redis.delete("stocks:{exchange}")`
2. Fetch top 100: `fetchTop100(exchange)`
3. Store in Redis:
   - Sorted set: `ZADD stocks:{exchange} {rank} {reutersCode}`
   - Hash: `HSET stockRank:{exchange}:{code} ...`

##### `fetchTop100(String exchange)`
**Purpose**: Fetch top 100 stocks from Naver API

**API URLs**:
- Foreign: `https://api.stock.naver.com/stock/exchange/{exchange}/marketValue?page={page}&pageSize=20`
- Domestic: `https://m.stock.naver.com/api/stocks/marketValue/all?page={page}&pageSize=20`

**Returns**: `List<StockInfo>` with reutersCode and marketValue

**Pagination**:
- 20 stocks per page
- 5 pages to get 100 stocks

#### Configuration:

**Properties**:
```properties
naver.stock.api.base=https://api.stock.naver.com/stock/exchange
naver.stock.domestic.api.base=https://m.stock.naver.com/api/stocks/marketValue
```

#### Performance Impact:

**Why Disabled?**:
- Slows server startup significantly
- 5 API calls on startup (5 exchanges)
- Each call fetches 5 pages (25 total API calls)
- Consider enabling only in production

**Enable**: Uncomment `@Component` annotation

---

## AI Agent Test Pages

### 5. $page$ai Package

**Purpose**: Test pages for AI-powered financial advisor features

**Package**: `com.konkuk.moneymate.common.templates.$page$ai`

**Structure**:
```
$page$ai/
├── controller/
│   ├── TemplatesPageController.java    # Page routes
│   └── TemplatesRestController.java    # API endpoints
└── service/
    ├── AgentTemplateService1.java      # AI service v1
    └── AgentTemplateService2.java      # AI service v2
```

#### TemplatesPageController

**Purpose**: Serve AI agent test pages

**Type**: `@Controller`

**Base URL**: `/test/page`

**Endpoints**:

##### `GET /test/page/agent/v1`
**Description**: AI Agent test page v1 (basic agent)  
**View**: `ai/agent-template-page-ver1.html`  
**Features**: Basic AI query/response

##### `GET /test/page/agent/v2`
**Description**: AI Agent test page v2 (asset analysis)  
**View**: `ai/agent-template-page-ver2.html`  
**Features**: Asset analysis with access token

##### `GET /test/page/agent/v3`
**Description**: AI Agent test page v3 (portfolio analysis)  
**View**: `ai/agent-template-page-ver3.html`  
**Features**: Portfolio analysis with access token

**Templates**: Located in `src/main/resources/templates/ai/`

**Access**: Public (defined in PERMIT_ALL_PATTERNS)

#### TemplatesRestController

**Purpose**: REST API endpoints for AI agent streaming

**Type**: `@RestController`

**Base URL**: `/test/page`

**Endpoints** (detailed in AI package documentation):
- `POST /test/page/agent/stream` - Basic streaming
- `POST /test/page/agent/stream/system` - With system prompt
- `POST /test/page/agent/stream/custom` - Custom prompt

**Response Type**: `TEXT_EVENT_STREAM` (Server-Sent Events)

---

## Security Configuration

### Public Endpoints

All endpoints in this package are public (no authentication required):

**Defined in** `SecurityConfig.PERMIT_ALL_PATTERNS`:
```java
"/test/page/stock"
"/test/page/agent/**"
"/test/page/agent/stream"
"/api/proxy/naver-stock/**"
"/api/proxy/eodhd/**"
```

**Why Public?**:
- Test pages for development
- API proxies need to be accessible from frontend
- No sensitive data exposure (only public market data)

---

## External API Dependencies

### Naver Stock API

**Base URLs**:
- Mobile: `https://m.stock.naver.com/api/`
- Desktop: `https://api.stock.naver.com/`

**Rate Limits**: Unknown (not documented)

**Authentication**: None required

**CORS**: Restricted (requires proxy)

### EODHD API

**Base URL**: `https://eodhd.com/api/`

**Authentication**: API token in query string

**Rate Limits**: Depends on subscription plan

**Documentation**: https://eodhd.com/financial-apis/

---

## Best Practices

### API Proxy Development

1. **Error Handling**:
   - Always catch exceptions
   - Return appropriate HTTP status codes
   - Include error message in response body

2. **Headers**:
   - Set `User-Agent` for APIs that require it
   - Pass through necessary headers
   - Don't expose internal headers

3. **Rate Limiting**:
   - Implement caching for frequently requested data
   - Consider adding rate limiting middleware
   - Log API usage for monitoring

4. **Security**:
   - Never expose API keys in responses
   - Validate input parameters
   - Sanitize error messages

### Scheduled Tasks

1. **Performance**:
   - Use `@Async` for heavy tasks
   - Implement backoff strategy for failures
   - Monitor execution time

2. **Error Handling**:
   - Log all errors
   - Don't let one failure stop others
   - Implement retry logic

3. **Resource Management**:
   - Reuse HTTP clients
   - Close resources properly
   - Set appropriate timeouts

---

## Testing

### Manual Testing Endpoints

**Stock Test Page**:
```
http://localhost:8080/test/page/stock
```

**Naver Stock Proxy**:
```
http://localhost:8080/api/proxy/naver-stock/realtime?ticker=005930&region=KR&exchange=KRX
http://localhost:8080/api/proxy/naver-stock/index?nation=KOR
http://localhost:8080/api/proxy/naver-stock/domestic?type=market&page=1&pageSize=20
```

**EODHD Proxy**:
```
http://localhost:8080/api/proxy/eodhd/realtime/15min?ticker=AAPL&market=US
http://localhost:8080/api/proxy/eodhd/bulk?market=US
```

**AI Agent Pages**:
```
http://localhost:8080/test/page/agent/v1
http://localhost:8080/test/page/agent/v2
http://localhost:8080/test/page/agent/v3
```

### Unit Testing Proxies

```java
@SpringBootTest
@AutoConfigureMockMvc
class NaverStockProxyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testGetRealtimeStock() throws Exception {
        mockMvc.perform(get("/api/proxy/naver-stock/realtime")
                .param("ticker", "005930")
                .param("region", "KR")
                .param("exchange", "KRX"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stockName").exists());
    }
}
```

---

## Debugging

### Enable Debug Logging

```properties
logging.level.com.konkuk.moneymate.common.templates=DEBUG
```

### Common Issues

**Issue**: Naver API returns 403 Forbidden  
**Solution**: Check User-Agent header is set

**Issue**: EODHD API returns 401 Unauthorized  
**Solution**: Verify API token is valid

**Issue**: Redis data not updating  
**Solution**: Check if `MarketValueRankingRefresher` is enabled (@Component)

**Issue**: Test pages not loading  
**Solution**: Verify Thymeleaf templates exist in `resources/templates/`

---

## Future Enhancements

- [ ] Implement response caching for API proxies
- [ ] Add rate limiting per IP/user
- [ ] Create unified proxy interface
- [ ] Add WebSocket support for real-time updates
- [ ] Implement API key rotation
- [ ] Add monitoring and alerting
- [ ] Enable `MarketValueRankingRefresher` with optimization
- [ ] Add more exchange support (Japan, Hong Kong, etc.)

---

**Package Owner**: Common Utilities Team  
**Last Updated**: November 2024  
**Previous Location**: `auth.templates` (moved to `common.templates`)  
**Related Documentation**:
- [Auth Package: ../../auth/CLAUDE.md](../../auth/CLAUDE.md)
- [AI Package: ../../ai/CLAUDE.md](../../ai/CLAUDE.md)
