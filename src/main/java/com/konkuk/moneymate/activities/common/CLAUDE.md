# Common Activities Package (`com.konkuk.moneymate.activities.common`)

## Overview

The `common` package contains shared DTOs and utilities used across multiple activity domains. It provides reusable components for date handling and other common functionality.

## Package Structure

```
common/
└── dto/
    └── DateRequestDto.java    # Standard date range request DTO
```

## Components

### DateRequestDto

**Purpose**: Standard DTO for date range queries across all activities

**Fields**:
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateRequestDto {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}
```

**Usage**: Used in controllers for filtering data by date range

**Example**:
```java
// In Controller
@GetMapping("/transactions")
public ResponseEntity<?> getTransactions(
    @ModelAttribute DateRequestDto dateRequest
) {
    return transactionService.getTransactions(
        userId, 
        dateRequest.getStartDate(), 
        dateRequest.getEndDate()
    );
}

// Request
GET /api/transaction?startDate=2024-01-01&endDate=2024-12-31
```

**Validation**:
- Both dates are optional
- If not provided, use defaults (e.g., current month, year-to-date)
- startDate should be before or equal to endDate

**Standard Usage Across Domains**:
- **Transactions**: Filter by transaction date
- **Assets**: Historical asset value queries
- **News**: Filter news by publication date
- **Statistics**: Date range for statistical queries

---

## Best Practices

1. **Consistent Date Format**: Always use ISO-8601 format (YYYY-MM-DD)
2. **Validation**: Validate date ranges in service layer
3. **Defaults**: Provide sensible defaults when dates are not specified
4. **Time Zones**: Consider time zone when comparing dates
5. **Null Handling**: Handle null dates gracefully

---

## Future Enhancements

- [ ] Add time component (LocalDateTime) support
- [ ] Add preset date ranges (THIS_MONTH, LAST_MONTH, etc.)
- [ ] Add validation annotations
- [ ] Add time zone support

---

**Package Owner**: Common Utilities Team  
**Last Updated**: November 2024  
**Related Documentation**:
- [Parent: activities/README.md](../README.md)

