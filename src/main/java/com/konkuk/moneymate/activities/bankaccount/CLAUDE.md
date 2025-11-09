# Bank Account Package (`com.konkuk.moneymate.activities.bankaccount`)

## Overview

The `bankaccount` package manages user bank accounts, transactions, and account stocks (stock holdings linked to accounts). It provides APIs for account management, transaction tracking, and consumption analysis.

## Package Structure

```
bankaccount/
├── controller/
│   └── BankAccountController.java     # REST API for bank accounts & transactions
├── dto/
│   ├── AccountStockId.java            # Composite key for account stocks
│   ├── BankAccountDto.java            # Bank account data transfer
│   └── TransactionDto.java            # Transaction data transfer
├── entity/
│   ├── AccountStock.java              # Stock holdings in account
│   ├── BankAccount.java               # Bank account entity
│   └── Transaction.java               # Transaction entity
├── enums/
│   ├── AccountType.java               # Account types (CHECKING, SAVINGS, etc.)
│   ├── BankCode.java                  # Bank identifier codes
│   └── TransactionCategory.java       # Transaction categories
├── repository/
│   ├── BankAccountRepository.java     # Bank account data access
│   └── TransactionRepository.java     # Transaction data access
├── service/
│   └── BankAccountService.java        # Business logic
├── BankAccountValidator.java          # Account validation logic
└── TransactionCategoryConverter.java  # JPA converter for categories
```

## Core Components

### 1. BankAccountController

**Purpose**: REST API endpoints for bank accounts and transactions

**Base URL**: `/api/bankaccount` and `/api/transaction`

**Endpoints**:

#### Bank Account Endpoints

##### `GET /api/bankaccount/all`
**Description**: Get all bank accounts for authenticated user  
**Headers**: `Authorization: Bearer <token>`  
**Response**: List of BankAccountDto

##### `POST /api/bankaccount`
**Description**: Create new bank account  
**Headers**: `Authorization: Bearer <token>`  
**Request Body**: BankAccountDto  
**Response**: Created bank account

##### `GET /api/bankaccount/{accountId}`
**Description**: Get specific bank account details  
**Headers**: `Authorization: Bearer <token>`  
**Path Variable**: `accountId`  
**Response**: BankAccountDto

##### `DELETE /api/bankaccount/{accountId}`
**Description**: Delete bank account  
**Headers**: `Authorization: Bearer <token>`  
**Path Variable**: `accountId`  
**Response**: Success message

#### Transaction Endpoints

##### `GET /api/transaction`
**Description**: Get transactions for user  
**Headers**: `Authorization: Bearer <token>`  
**Query Params**: `startDate`, `endDate`, `category` (optional)  
**Response**: List of TransactionDto

##### `POST /api/transaction`
**Description**: Create new transaction  
**Headers**: `Authorization: Bearer <token>`  
**Request Body**: TransactionDto  
**Response**: Created transaction

##### `GET /api/transaction/monthly-summary`
**Description**: Get monthly transaction summary  
**Headers**: `Authorization: Bearer <token>`  
**Query Params**: `year`, `month`  
**Response**: Summary with income/expense breakdown

---

### 2. BankAccountService

**Purpose**: Business logic for bank accounts and transactions

**Key Methods**:

##### `getAllBankAccounts(String userId)`
- Retrieve all bank accounts for user
- Calculate current balance
- Return list of BankAccountDto

##### `createBankAccount(BankAccountDto dto, String userId)`
- Validate account data
- Check for duplicate accounts
- Create and save account
- Return created account

##### `getBankAccountById(Long accountId, String userId)`
- Retrieve account by ID
- Verify user ownership
- Return account details

##### `deleteBankAccount(Long accountId, String userId)`
- Verify user ownership
- Check for dependent transactions
- Delete account

##### `createTransaction(TransactionDto dto, String userId)`
- Validate transaction data
- Update account balance
- Save transaction
- Return created transaction

##### `getTransactions(String userId, LocalDate start, LocalDate end, TransactionCategory category)`
- Retrieve filtered transactions
- Apply date and category filters
- Return list of transactions

##### `getMonthlySummary(String userId, int year, int month)`
- Calculate total income
- Calculate total expenses
- Breakdown by category
- Return summary object

---

### 3. Entities

#### BankAccount Entity

**Table**: `bank_account`

**Key Fields**:
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@ManyToOne
@JoinColumn(name = "user_id")
private User user;

private String accountNumber;
private String accountName;

@Enumerated(EnumType.STRING)
private AccountType accountType;

@Enumerated(EnumType.STRING)
private BankCode bankCode;

private BigDecimal balance;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
```

**Relationships**:
- Many-to-One with User
- One-to-Many with Transaction
- One-to-Many with AccountStock

#### Transaction Entity

**Table**: `transaction`

**Key Fields**:
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@ManyToOne
@JoinColumn(name = "bank_account_id")
private BankAccount bankAccount;

@ManyToOne
@JoinColumn(name = "user_id")
private User user;

private BigDecimal amount;
private String description;

@Convert(converter = TransactionCategoryConverter.class)
private TransactionCategory category;

private LocalDateTime transactionDate;
private LocalDateTime createdAt;
```

**Transaction Types**:
- Income (positive amount)
- Expense (negative amount)

#### AccountStock Entity

**Table**: `account_stock`

**Purpose**: Track stock holdings within bank accounts

**Key Fields**:
```java
@EmbeddedId
private AccountStockId id;  // Composite key (accountId, stockId)

@ManyToOne
@JoinColumn(name = "bank_account_id")
private BankAccount bankAccount;

@ManyToOne
@JoinColumn(name = "stock_id")
private Stock stock;

private Integer quantity;
private BigDecimal averagePrice;
private LocalDateTime purchaseDate;
```

---

### 4. Enums

#### AccountType
```java
public enum AccountType {
    CHECKING("입출금계좌"),
    SAVINGS("저축예금"),
    DEPOSIT("정기예금"),
    STOCK("주식계좌"),
    CRYPTO("가상화폐계좌");
}
```

#### BankCode
```java
public enum BankCode {
    KB("004", "국민은행"),
    SHINHAN("088", "신한은행"),
    WOORI("020", "우리은행"),
    HANA("081", "하나은행"),
    NH("011", "농협은행"),
    // ... more banks
}
```

#### TransactionCategory
```java
public enum TransactionCategory {
    FOOD("식비"),
    TRANSPORT("교통비"),
    SHOPPING("쇼핑"),
    UTILITY("공과금"),
    ENTERTAINMENT("문화생활"),
    SALARY("급여"),
    BONUS("보너스"),
    // ... more categories
}
```

---

### 5. DTOs

#### BankAccountDto
```java
Long id;
String accountNumber;
String accountName;
AccountType accountType;
BankCode bankCode;
BigDecimal balance;
LocalDateTime createdAt;
LocalDateTime updatedAt;
```

#### TransactionDto
```java
Long id;
Long bankAccountId;
BigDecimal amount;
String description;
TransactionCategory category;
LocalDateTime transactionDate;
LocalDateTime createdAt;
```

#### AccountStockId (Composite Key)
```java
@Embeddable
public class AccountStockId implements Serializable {
    private Long bankAccountId;
    private Long stockId;
}
```

---

### 6. Validators & Converters

#### BankAccountValidator
**Purpose**: Validate bank account data

**Methods**:
- `validateAccountNumber(String accountNumber)` - Check format
- `validateBankCode(BankCode bankCode)` - Verify valid bank
- `validateBalance(BigDecimal balance)` - Ensure non-negative

#### TransactionCategoryConverter
**Purpose**: JPA converter for TransactionCategory enum

**Type**: `@Converter(autoApply = true)`

**Converts**: Enum ↔ Database String

---

## Business Logic

### Account Management
1. **Create Account**: Validate user, bank code, account number
2. **Update Balance**: Automatically update on transaction
3. **Delete Account**: Check for transactions, handle cascade

### Transaction Processing
1. **Validate Amount**: Non-zero, appropriate sign
2. **Update Balance**: Add (income) or subtract (expense)
3. **Categorize**: Auto-categorize based on description
4. **Record**: Save with timestamp

### Monthly Summary
1. **Aggregate Transactions**: Sum by category
2. **Calculate Totals**: Total income, total expense
3. **Net Flow**: Income - Expenses
4. **Category Breakdown**: Top spending categories

---

## Dependencies

### External
- Spring Data JPA
- Spring Web
- Lombok

### Internal
- `activities.user.entity.User` - User entity
- `activities.stock.entity.Stock` - Stock entity for account stocks
- `auth.service.JwtService` - User authentication
- `common.ApiResponse` - Response wrapper

---

## Security

- All endpoints require authentication
- User can only access their own accounts/transactions
- Account operations validated against ownership
- Sensitive data (account numbers) logged securely

---

## API Response Format

#### 명세서 문서 참고

---

## Best Practices

1. **Balance Consistency**: Use transactions for balance updates
2. **Validation**: Validate all input data before persisting
3. **Categorization**: Auto-categorize transactions when possible
4. **Audit Trail**: Log all account/transaction operations
5. **Error Handling**: Return appropriate error messages

---

**Package Owner**: Bank Account Team  
**Last Updated**: November 2024  
**Related Documentation**:
- [Parent: activities/README.md](../README.md)
- [User Package: ../user/CLAUDE.md](../user/CLAUDE.md)
- [Stock Package: ../stock/CLAUDE.md](../stock/CLAUDE.md)

