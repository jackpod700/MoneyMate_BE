# Statistics Package (`com.konkuk.moneymate.activities.stats`)

## Overview

The `stats` package provides statistical data for financial analysis, including consumption patterns, income levels, and asset distribution by age demographics. It integrates with KOSIS (Korean Statistical Information Service) API and provides comparative analytics for users.

## Package Structure

```
stats/
├── controller/
│   ├── ConsumptionStatsController.java   # Consumption statistics API
│   ├── KosisStatsController.java         # KOSIS API integration
│   └── StatisticController.java          # General statistics API
├── dto/
│   ├── ConsumptionStatsResponse.java     # Consumption stats response
│   ├── KosisStatsDataRequest.java        # KOSIS API request
│   └── KosisStatsDataResponse.java       # KOSIS API response
├── entity/
│   ├── AssetStatsData.java               # Asset statistics by age
│   ├── ConsumptionStatsData.java         # Consumption statistics by age
│   └── IncomeStatsData.java              # Income statistics by age
├── repository/
│   ├── AssetStatsRepository.java         # Asset stats data access
│   ├── ConsumptionStatsRepository.java   # Consumption stats data access
│   └── IncomeStatsRepository.java        # Income stats data access
└── service/
    ├── AssetStatsService.java            # Asset statistics logic
    ├── ConsumptionStatsService.java      # Consumption statistics logic
    └── IncomeStatsService.java           # Income statistics logic
```

## Core Components

### 1. Controllers

#### ConsumptionStatsController

**Purpose**: API for consumption statistics

**Base URL**: `/api/stats/consumption`

**Endpoints**:

##### `GET /api/stats/consumption/by-age`
**Description**: Get consumption statistics by age group  
**Query Params**: `ageGroup` (20s, 30s, 40s, 50s, 60s)  
**Response**: ConsumptionStatsResponse

##### `GET /api/stats/consumption/compare`
**Description**: Compare user's consumption with age group average  
**Headers**: `Authorization: Bearer <token>`  
**Response**: Comparison data with percentile

#### KosisStatsController

**Purpose**: Integration with KOSIS API

**Base URL**: `/api/stats/kosis`

**Endpoints**:

##### `POST /api/stats/kosis/fetch`
**Description**: Fetch data from KOSIS API  
**Auth**: Admin only  
**Request Body**: KosisStatsDataRequest  
**Response**: KosisStatsDataResponse

##### `GET /api/stats/kosis/latest`
**Description**: Get latest KOSIS statistics  
**Query Params**: `category` (asset, income, consumption)  
**Response**: Latest statistics data

#### StatisticController

**Purpose**: General statistical queries

**Base URL**: `/api/stats`

**Endpoints**:

##### `GET /api/stats/asset-by-age`
**Description**: Get asset statistics by age group  
**Query Params**: `ageGroup`  
**Response**: Asset statistics

##### `GET /api/stats/income-by-age`
**Description**: Get income statistics by age group  
**Query Params**: `ageGroup`  
**Response**: Income statistics

##### `GET /api/stats/user-comparison`
**Description**: Compare user's financial status with peers  
**Headers**: `Authorization: Bearer <token>`  
**Response**: Comprehensive comparison (asset, income, consumption)

---

### 2. Services

#### ConsumptionStatsService

**Purpose**: Business logic for consumption statistics

**Key Methods**:

##### `getConsumptionByAge(String ageGroup)`
- Retrieve consumption stats for age group
- Calculate average consumption by category
- Return ConsumptionStatsResponse

##### `compareUserConsumption(String userId)`
- Get user's consumption data
- Calculate age group average
- Compute percentile ranking
- Return comparison object

##### `getTopCategories(String ageGroup)`
- Get top consumption categories
- Calculate percentage distribution
- Return sorted list

#### AssetStatsService

**Purpose**: Business logic for asset statistics

**Key Methods**:

##### `getAssetByAge(String ageGroup)`
- Retrieve asset stats for age group
- Calculate average asset by type
- Return asset distribution

##### `compareUserAssets(String userId)`
- Get user's asset data
- Calculate age group average
- Compute percentile ranking
- Return comparison object

#### IncomeStatsService

**Purpose**: Business logic for income statistics

**Key Methods**:

##### `getIncomeByAge(String ageGroup)`
- Retrieve income stats for age group
- Calculate average income
- Return income statistics

##### `compareUserIncome(String userId)`
- Get user's income data
- Calculate age group average
- Compute percentile ranking
- Return comparison object

---

### 3. Entities

#### AssetStatsData

**Table**: `asset_stats_data`

**Purpose**: Store aggregate asset statistics by age

**Key Fields**:
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String ageGroup;           // "20s", "30s", "40s", "50s", "60s"
private BigDecimal avgTotalAsset;  // Average total assets
private BigDecimal avgCash;        // Average cash holdings
private BigDecimal avgStock;       // Average stock holdings
private BigDecimal avgRealEstate;  // Average real estate
private BigDecimal avgCrypto;      // Average crypto holdings
private LocalDate dataDate;        // Data collection date
private String source;             // Data source (KOSIS, etc.)
```

#### ConsumptionStatsData

**Table**: `consumption_stats_data`

**Purpose**: Store aggregate consumption statistics by age

**Key Fields**:
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String ageGroup;
private BigDecimal avgTotalConsumption;
private BigDecimal avgFood;
private BigDecimal avgTransport;
private BigDecimal avgShopping;
private BigDecimal avgUtility;
private BigDecimal avgEntertainment;
private LocalDate dataDate;
private String source;
```

#### IncomeStatsData

**Table**: `income_stats_data`

**Purpose**: Store aggregate income statistics by age

**Key Fields**:
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String ageGroup;
private BigDecimal avgAnnualIncome;
private BigDecimal avgMonthlyIncome;
private BigDecimal avgBonus;
private BigDecimal medianIncome;
private LocalDate dataDate;
private String source;
```

---

### 4. DTOs

#### ConsumptionStatsResponse
```java
String ageGroup;
BigDecimal avgTotalConsumption;
Map<String, BigDecimal> categoryBreakdown;
List<String> topCategories;
LocalDate dataDate;
```

#### KosisStatsDataRequest
```java
String apiKey;
String statisticsCode;   // KOSIS statistics code
String periodCode;       // Period (year, quarter, month)
String itemCode;         // Item code
```

#### KosisStatsDataResponse
```java
String statisticsName;
List<Map<String, Object>> data;
LocalDateTime fetchedAt;
```

---

## Business Logic

### Age Group Classification
```java
public static String getAgeGroup(int age) {
    if (age < 30) return "20s";
    else if (age < 40) return "30s";
    else if (age < 50) return "40s";
    else if (age < 60) return "50s";
    else return "60s";
}
```

### Percentile Calculation
```java
public double calculatePercentile(BigDecimal userValue, String ageGroup, String metric) {
    // Get sorted list of values for age group
    List<BigDecimal> values = getSortedValues(ageGroup, metric);
    
    // Find user's position
    int position = Collections.binarySearch(values, userValue);
    
    // Calculate percentile
    return (double) position / values.size() * 100;
}
```

### Comparison Logic
1. **Get User Data**: Retrieve user's financial data
2. **Get Age Group**: Determine user's age group
3. **Get Averages**: Fetch age group statistics
4. **Calculate Percentile**: Determine user's ranking
5. **Generate Insights**: Provide personalized insights

---

## KOSIS API Integration

### API Configuration
```properties
kosis.api.url=https://kosis.kr/openapi/statisticsData.do
kosis.api.key=${KOSIS_API_KEY}
```

### Data Collection
```java
@Scheduled(cron = "0 0 3 1 * *")  // First day of month at 3 AM
public void updateStatsFromKosis() {
    // Fetch asset statistics
    fetchAssetStats();
    
    // Fetch income statistics
    fetchIncomeStats();
    
    // Fetch consumption statistics
    fetchConsumptionStats();
}
```

### Statistics Codes
- **Asset Statistics**: `DT_1L6E001`
- **Income Statistics**: `DT_1L6E002`
- **Consumption Statistics**: `DT_1L6E003`

---

## Dependencies

### External
- Spring Data JPA
- Spring Web
- Spring Scheduling
- OkHttp (for KOSIS API)

### Internal
- `activities.user.entity.User` - User data
- `activities.assets.entity.Asset` - User assets
- `activities.bankaccount.entity.Transaction` - User transactions
- `auth.service.JwtService` - User authentication

---

## Security

- Statistics endpoints are public (anonymized data)
- User comparison requires authentication
- KOSIS API fetch requires admin authentication
- Personal data never exposed in aggregates

---

## API Response Format

```json
{
  "status": "OK",
  "message": "소비 통계 조회 성공",
  "data": {
    "startDate": "2023-08-10",
    "endDate": "2025-08-16",
    "categoryTotals": {
      "주거/공과금": 0,
      "교통/자동차": 31575,
      "식비": 446600,
      "카페": 19600,
      "생활/쇼핑": 419900,
      "편의점": 104400,
      "의료/건강": 0,
      "문화생활/취미": 123000,
      "여행/숙박": 0,
      "교육": 0,
      "정기결제": 7890,
      "서적": 85400,
      "간편결제": 91310,
      "기타지출": 0,
    }
  }
}
```


---

## Best Practices

1. **Data Privacy**: Never expose individual user data
2. **Anonymization**: Aggregate data in groups of 100+ users
3. **Data Freshness**: Update statistics monthly
4. **Performance**: Cache statistics queries
5. **Error Handling**: Handle KOSIS API failures gracefully

---

## Future Enhancements

- [ ] Add regional statistics comparison
- [ ] Implement trend analysis (YoY, MoM)
- [ ] Add predictive analytics
- [ ] Support custom age ranges
- [ ] Add income bracket analysis
- [ ] Implement visualization APIs
- [ ] Add export to PDF/Excel

---

**Package Owner**: Statistics Team  
**Last Updated**: November 2024  
**Related Documentation**:
- [Parent: activities/README.md](../README.md)
- [Assets Package: ../assets/CLAUDE.md](../assets/CLAUDE.md)
- [Bank Account Package: ../bankaccount/CLAUDE.md](../bankaccount/CLAUDE.md)
- [KOSIS API Documentation](https://kosis.kr/openapi/)

