# Financial Products Package (`com.konkuk.moneymate.activities.financial`)

## Overview

The `financial` package manages financial product information including deposits, savings, credit loans, mortgage loans, and rental house loans. It integrates with the Korean Financial Services Commission's Finlife API to provide up-to-date product information.

## Package Structure

```
financial/
├── controller/
│   └── FinancialProductController.java    # REST API for financial products
├── dto/
│   ├── FinancialProductDto.java           # Base product DTO
│   ├── DepositProductDto.java             # Deposit product DTO
│   ├── SavingProductDto.java              # Saving product DTO
│   ├── CreditLoanProductDto.java          # Credit loan product DTO
│   ├── MortgageLoanProductDto.java        # Mortgage loan product DTO
│   └── RentHouseLoanProductDto.java       # Rental house loan product DTO
├── entity/
│   ├── FinancialProduct.java              # Base product entity
│   ├── FinancialCompany.java              # Financial company entity
│   ├── FinancialCompanyRegion.java        # Company region entity
│   ├── DepositProduct.java                # Deposit product entity
│   ├── DepositProductOption.java          # Deposit options
│   ├── SavingProduct.java                 # Saving product entity
│   ├── SavingProductOption.java           # Saving options
│   ├── CreditLoanProduct.java             # Credit loan entity
│   ├── CreditLoanProductOption.java       # Credit loan options
│   ├── MortgageLoanProduct.java           # Mortgage loan entity
│   ├── MortgageLoanProductOption.java     # Mortgage options
│   ├── RentHouseLoanProduct.java          # Rental house loan entity
│   └── RentHouseLoanProductOption.java    # Rental house loan options
├── enums/
│   ├── FinancialGroupCode.java            # Bank, Savings Bank, etc.
│   ├── FinancialCompanyRegionCode.java    # Regional codes
│   ├── InterestType.java                  # Simple, Compound interest
│   ├── JoinWay.java                       # Join methods
│   ├── RsrvType.java                      # Reserve type
│   ├── RpayType.java                      # Repayment type
│   ├── LendRateType.java                  # Lending rate type
│   ├── CrdtLendRateType.java              # Credit loan rate type
│   └── CrdtPrdtType.java                  # Credit product type
├── repository/
│   ├── FinancialProductRepository.java
│   ├── FinancialCompanyRepository.java
│   ├── FinancialCompanyRegionRepository.java
│   ├── DepositProductRepository.java
│   ├── DepositProductOptionRepository.java
│   ├── SavingProductRepository.java
│   ├── SavingProductOptionRepository.java
│   ├── CreditLoanProductRepository.java
│   ├── CreditLoanProductOptionRepository.java
│   ├── MortgageLoanProductRepository.java
│   ├── MortgageLoanProductOptionRepository.java
│   ├── RentHouseLoanProductRepository.java
│   └── RentHouseLoanProductOptionRepository.java
├── service/
│   └── FinancialProductService.java       # Product business logic
└── util/
    ├── FinancialCompanyFetcher.java       # Fetch company data
    ├── FinlifeApiResponse.java            # API response wrapper
    └── FinlifeFunctionParam.java          # API parameter builder
```

## Core Components

### 1. FinancialProductController

**Purpose**: REST API endpoints for financial product queries

**Base URL**: `/api/financial`

**Endpoints**:

#### `GET /api/financial/deposit`
**Description**: Get deposit products  
**Query Params**: `bankCode`, `minRate`, `maxRate`, `period` (optional)  
**Response**: List of DepositProductDto

#### `GET /api/financial/saving`
**Description**: Get saving products  
**Query Params**: `bankCode`, `minRate`, `maxRate`, `period` (optional)  
**Response**: List of SavingProductDto

#### `GET /api/financial/credit-loan`
**Description**: Get credit loan products  
**Query Params**: `bankCode`, `loanType`, `maxRate` (optional)  
**Response**: List of CreditLoanProductDto

#### `GET /api/financial/mortgage-loan`
**Description**: Get mortgage loan products  
**Query Params**: `bankCode`, `loanType`, `maxRate` (optional)  
**Response**: List of MortgageLoanProductDto

#### `GET /api/financial/rent-house-loan`
**Description**: Get rental house loan products  
**Query Params**: `bankCode`, `loanType`, `maxRate` (optional)  
**Response**: List of RentHouseLoanProductDto

#### `GET /api/financial/companies`
**Description**: Get financial company list  
**Query Params**: `groupCode`, `regionCode` (optional)  
**Response**: List of financial companies

#### `POST /api/financial/refresh`
**Description**: Refresh product data from Finlife API  
**Auth**: Admin only  
**Response**: Success message with updated counts

---

### 2. FinancialProductService

**Purpose**: Business logic for financial product management

**Key Methods**:

##### `getDepositProducts(String bankCode, Double minRate, Double maxRate, Integer period)`
- Filter deposit products by criteria
- Apply rate and period filters
- Return sorted list by interest rate

##### `getSavingProducts(String bankCode, Double minRate, Double maxRate, Integer period)`
- Filter saving products by criteria
- Apply rate and period filters
- Return sorted list by interest rate

##### `getCreditLoanProducts(String bankCode, String loanType, Double maxRate)`
- Filter credit loan products
- Apply loan type and rate filters
- Return sorted list by interest rate

##### `getMortgageLoanProducts(String bankCode, String loanType, Double maxRate)`
- Filter mortgage loan products
- Apply loan type and rate filters
- Return sorted list by interest rate

##### `getRentHouseLoanProducts(String bankCode, String loanType, Double maxRate)`
- Filter rental house loan products
- Apply loan type and rate filters
- Return sorted list by interest rate

##### `refreshAllProducts()`
- Fetch latest data from Finlife API
- Update database with new products
- Delete discontinued products
- Return update statistics

---

### 3. Entities

#### FinancialProduct (Base Entity)

**Table**: `financial_product`

**Inheritance**: `@Inheritance(strategy = InheritanceType.JOINED)`

**Key Fields**:
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String productCode;      // Product code from Finlife
private String productName;      // Product name
private String financialCompanyCode;  // Company code

@Enumerated(EnumType.STRING)
private FinancialGroupCode financialGroupCode;

@Enumerated(EnumType.STRING)
private JoinWay joinWay;         // Online, Branch, etc.

private String specialCondition;  // Special conditions
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
```

#### DepositProduct

**Table**: `deposit_product`

**Additional Fields**:
```java
@Enumerated(EnumType.STRING)
private InterestType interestType;  // Simple, Compound

@Enumerated(EnumType.STRING)
private RsrvType rsrvType;          // Free, Fixed

private Integer maxPeriod;          // Maximum contract period (months)
```

#### Product Options (e.g., DepositProductOption)

**Purpose**: Store various interest rate options for each product

**Key Fields**:
```java
@ManyToOne
@JoinColumn(name = "deposit_product_id")
private DepositProduct depositProduct;

private Integer savePeriod;         // Save period (months)
private Double intrRate;            // Base interest rate
private Double intrRate2;           // Preferential interest rate
```

---

### 4. Enums

#### FinancialGroupCode
```java
public enum FinancialGroupCode {
    BANK("020000", "은행"),
    SAVINGS_BANK("030200", "저축은행"),
    CREDIT_UNION("030300", "신용협동조합"),
    MUTUAL_SAVINGS("050000", "상호저축은행"),
    // ... more types
}
```

#### InterestType
```java
public enum InterestType {
    SIMPLE("단리"),
    COMPOUND("복리");
}
```

#### JoinWay
```java
public enum JoinWay {
    ONLINE("인터넷"),
    BRANCH("영업점"),
    SMARTPHONE("스마트폰");
}
```

---

### 5. Finlife API Integration

#### FinancialCompanyFetcher

**Purpose**: Fetch financial company data from Finlife API

**API Endpoints**:
- Deposit: `http://finlife.fss.or.kr/finlifeapi/depositProductsSearch.json`
- Saving: `http://finlife.fss.or.kr/finlifeapi/savingProductsSearch.json`
- Credit Loan: `http://finlife.fss.or.kr/finlifeapi/creditLoanProductsSearch.json`
- Mortgage: `http://finlife.fss.or.kr/finlifeapi/mortgageLoanProductsSearch.json`
- Rent House: `http://finlife.fss.or.kr/finlifeapi/rentHouseLoanProductsSearch.json`

**API Key**: Configured in `application.properties`

**Rate Limiting**: Respect API rate limits

#### FinlifeApiResponse

**Purpose**: Wrapper for Finlife API responses

**Structure**:
```java
{
  "result": {
    "baseList": [...],      // Product base information
    "optionList": [...]     // Product option information
  }
}
```

#### FinlifeFunctionParam

**Purpose**: Build query parameters for Finlife API

**Methods**:
- `withTopFinGrpNo(String code)` - Financial group filter
- `withPageNo(int page)` - Pagination
- `build()` - Generate query string

---

## Business Logic

### Product Search & Filtering
1. **Rate Filtering**: Filter by interest rate range
2. **Period Filtering**: Filter by contract period
3. **Bank Filtering**: Filter by specific banks
4. **Sorting**: Sort by interest rate (descending)

### Product Refresh Strategy
1. **Fetch from API**: Get latest data from Finlife
2. **Compare with DB**: Identify new, updated, and discontinued products
3. **Update DB**: Add new, update existing, mark discontinued
4. **Cleanup**: Remove old discontinued products

### Product Recommendations
1. **User Profile**: Consider user age, income, assets
2. **Risk Profile**: Match products to risk tolerance
3. **Financial Goals**: Recommend based on goals
4. **Comparison**: Show top products side-by-side

---

## Dependencies

### External
- Spring Data JPA
- Spring Web
- OkHttp (for Finlife API calls)
- Gson (JSON parsing)

### Internal
- `common.ApiResponse` - Response wrapper
- `auth.service.JwtService` - User authentication

---

## Security

- Product query endpoints are public
- Refresh endpoint requires admin authentication
- API key stored securely in environment variables

---

## API Response Format

```json
{
  "status":"OK",
  "message":"은행상품(정기예금) 조회 완료",
  "data":[{
    "bankName":	"우리은행",
    "productName":	"WON플러스예금",
    "intrRate":"2.45",
    "maxIntrRate":	"2.45",
    "intrType":	"단리",
    "joinWay":	"인터넷,스마트폰,전화(텔레뱅킹)",
    "mtrtInt":	"만기 후\n- 1개월이내 : 만기시점약정이율×50%\n- 1개월초과 6개월이내: 만기시점약정이율×30%\n- 6개월초과 : 만기시점약정이율×20%\n\n※ 만기시점 약정이율 : 일반정기예금 금리",
    "spclCnd":	"해당사항 없음",
    "joinDeny":	"제한없음",
    "joinMember":	"실명의 개인",
    "etcNote":	"- 가입기간: 1~36개월\n- 최소가입금액: 1만원 이상\n- 만기일을 일,월 단위로 자유롭게 선택 가능\n- 만기해지 시 신규일 당시 영업점과 인터넷 홈페이지에 고시된 계약기간별 금리 적용",
    "maxLimit":	null,
    "dclsStrtDay":	"20250820",
    "dclsEndDay":	null,
    "url":	"https://spot.wooribank.com/pot/Dream?withyou=po",
    "callNum"	:"15885000"
  },
    ...
  ]
}
```

---

## Best Practices

1. **Data Freshness**: Refresh product data daily
2. **Caching**: Cache product list for performance
3. **Error Handling**: Handle API failures gracefully
4. **Validation**: Validate filter parameters
5. **Logging**: Log API calls and errors

---

## Future Enhancements

- [ ] Add product comparison feature
- [ ] Implement user favorites
- [ ] Add email alerts for rate changes
- [ ] Support more product types
- [ ] Add product rating/review system

---

**Package Owner**: Financial Products Team  
**Last Updated**: November 2024  
**Related Documentation**:
- [Parent: activities/README.md](../README.md)
- [Finlife API Documentation](http://finlife.fss.or.kr)

