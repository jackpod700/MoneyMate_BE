# Assets Package (`com.konkuk.moneymate.activities.assets`)

## Overview

The `assets` package manages user assets including stocks, cash, real estate, and other financial holdings. It provides APIs for asset creation, retrieval, valuation, and historical tracking.

## Package Structure

```
assets/
├── controller/
│   └── AssetController.java          # REST API endpoints for assets
├── dto/
│   ├── AssetDto.java                 # Asset data transfer object
│   └── AssetHistoryDto.java          # Asset history data transfer
├── entity/
│   └── Asset.java                    # Asset JPA entity
├── repository/
│   └── AssetRepository.java          # Asset data access
└── service/
    └── AssetService.java             # Asset business logic
```

## Core Components

### 1. AssetController

**Purpose**: REST API endpoints for asset management

**Base URL**: `/api/asset`

**Endpoints**:

#### `GET /api/asset/all`
**Description**: Get all assets for the authenticated user  
**Headers**: `Authorization: Bearer <token>`  
**Response**: List of all user assets with current values

#### `GET /api/asset/total`
**Description**: Get total asset value for the authenticated user  
**Headers**: `Authorization: Bearer <token>`  
**Response**: Total asset value in KRW

#### `POST /api/asset`
**Description**: Create new asset  
**Headers**: `Authorization: Bearer <token>`  
**Request Body**: AssetDto  
**Response**: Created asset with ID

#### `GET /api/asset/history`
**Description**: Get asset value history over time  
**Headers**: `Authorization: Bearer <token>`  
**Query Params**: `startDate`, `endDate`  
**Response**: List of AssetHistoryDto with timestamps and values

---

### 2. AssetService

**Purpose**: Business logic for asset management

**Key Methods**:

##### `getAllAssets(String userId)`
- Retrieve all assets for a user
- Calculate current values for stocks and crypto
- Return list of AssetDto

##### `getTotalAssetValue(String userId)`
- Sum all asset values for a user
- Include cash, stocks, real estate, crypto
- Return total value in KRW

##### `createAsset(AssetDto dto, String userId)`
- Validate asset data
- Create new asset entity
- Save to database
- Return created asset

##### `getAssetHistory(String userId, LocalDate startDate, LocalDate endDate)`
- Retrieve historical asset values
- Aggregate by date
- Return list of AssetHistoryDto

---

### 3. Asset Entity

**Purpose**: JPA entity representing user assets

**Table**: `asset`

**Key Fields**:
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@ManyToOne
@JoinColumn(name = "user_id")
private User user;

private String assetType;      // "STOCK", "CASH", "REAL_ESTATE", "CRYPTO"
private String assetName;      // Asset name/description
private BigDecimal amount;     // Quantity or value
private BigDecimal currentValue;  // Current market value
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
```

**Relationships**:
- Many-to-One with User entity

---

### 4. AssetRepository

**Purpose**: Data access layer for assets

**Type**: `JpaRepository<Asset, Long>`

**Custom Queries**:
```java
List<Asset> findByUserId(String userId);
BigDecimal sumCurrentValueByUserId(String userId);
List<Asset> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime start, LocalDateTime end);
```

---

### 5. DTOs

#### AssetDto
**Purpose**: Transfer asset data between layers

**Fields**:
```java
Long id;
String assetType;
String assetName;
BigDecimal amount;
BigDecimal currentValue;
LocalDateTime createdAt;
LocalDateTime updatedAt;
```

#### AssetHistoryDto
**Purpose**: Transfer historical asset data

**Fields**:
```java
LocalDate date;
BigDecimal totalValue;
Map<String, BigDecimal> assetBreakdown;  // By asset type
```

---

## Asset Types

### Supported Asset Types:
1. **STOCK** - Stock holdings
2. **CASH** - Cash and bank deposits
3. **REAL_ESTATE** - Real estate properties
4. **CRYPTO** - Cryptocurrency holdings
5. **OTHER** - Other assets

---

## Business Logic

### Asset Valuation
- **Stocks**: Fetch current price from stock API
- **Cash**: Use stored amount
- **Real Estate**: Use latest appraisal value
- **Crypto**: Fetch current price from crypto API

### Historical Tracking
- Snapshot asset values daily
- Store in asset history table
- Aggregate for reporting

---

## Dependencies

### External
- Spring Data JPA
- Spring Web

### Internal
- `activities.user.entity.User` - User entity
- `activities.stock.service.StockService` - Stock price lookup
- `auth.service.JwtService` - Extract current user from token

---

## Security

- All endpoints require authentication (Bearer token)
- Users can only access their own assets
- Asset operations validated against user ownership

---

## API Response Format

```json
{
  "status":"OK",
  "message":"보유주식 조회 성공",
  "data":[{
    "accountName":"NH투자증권",
    "stockName":"테슬라",
    "ticker":"TSLA",
    "quantity":"10",
    "totalPrice":"1000000.0"
    "profit":"30.0"
  },
    ...
  ]
}
```

---

## Best Practices

1. **Validation**: Always validate asset data before persisting
2. **Real-time Pricing**: Cache stock/crypto prices for performance
3. **Transactions**: Use @Transactional for consistency
4. **Error Handling**: Return appropriate HTTP status codes
5. **Logging**: Log asset operations for audit trail

---

**Package Owner**: Assets Team  
**Last Updated**: November 2024  
**Related Documentation**:
- [Parent: activities/README.md](../README.md)
- [Stock Package: ../stock/CLAUDE.md](../stock/CLAUDE.md)
- [User Package: ../user/CLAUDE.md](../user/CLAUDE.md)

