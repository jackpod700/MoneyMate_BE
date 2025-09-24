package com.konkuk.moneymate.activities.service;

import com.konkuk.moneymate.activities.dto.BankAccountDto;
import com.konkuk.moneymate.activities.dto.ConsumptionStatsResponse;
import com.konkuk.moneymate.activities.enums.TransactionCategory;
import com.konkuk.moneymate.activities.repository.TransactionRepository;
import com.konkuk.moneymate.auth.service.JwtService;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class ConsumptionStatsService {
    private final BankAccountService bankAccountService;
    private final TransactionRepository transactionRepository;
    private final JwtService jwtService;

    public ResponseEntity<?> consumptionStats(String startDay, String endDay, HttpServletRequest httpServletRequest) {
        try {
            String userUid = jwtService.getUserUid(httpServletRequest);

            LocalDate startDate = LocalDate.parse(startDay);
            LocalDate endDate   = LocalDate.parse(endDay);

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime   = endDate.plusDays(1).atStartOfDay();

            log.info("consumptionStats Request : startDate={}, endDate={}", startDateTime, endDateTime);

            /*
                switch (dateType.toUpperCase()) {
                    case "WEEK":   startDate = endDate.minusWeeks(1); break;
                    case "MONTH":  startDate = endDate.minusMonths(1); break;
                    case "3MONTH": startDate = endDate.minusMonths(3); break;
                    case "6MONTH": startDate = endDate.minusMonths(6); break;
                    case "YEAR":   startDate = endDate.minusYears(1); break;
                    default: throw new IllegalArgumentException("Invalid dateType: " + dateType);
                }
            */


            /**
             *
             */
            List<UUID> accountUids = bankAccountService.getAccountList(userUid).stream()
                    .map(BankAccountDto::getAccountUid)
                    .collect(Collectors.toList());
            log.info("Fetched {} accountUids for user {} : {}", accountUids.size(), userUid, accountUids);

            if (accountUids.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(
                        HttpStatus.OK.getReasonPhrase(),
                        ApiResponseMessage.CONSUMPTION_DATA_LOAD_SUCCESS.getMessage(),
                        new ConsumptionStatsResponse(startDate, endDate, Collections.emptyMap())
                ));
            }


            List<Object[]> results = transactionRepository.consumptionAmountsByCategory(accountUids, startDateTime, endDateTime);

            Map<String, Long> categoryTotals = new LinkedHashMap<>();
            for (TransactionCategory category : TransactionCategory.values()) {
                if (category.getFlow() == TransactionCategory.FlowType.OUTCOME
                        || category.getFlow() == TransactionCategory.FlowType.BOTH) {
                    categoryTotals.put(category.getDisplayName(), 0L);
                }
            }

            /**
             * ENUM <-> DB 매핑 변환합ㅣㄴ다
             */
            for (Object[] row : results) {
                TransactionCategory category = (TransactionCategory) row[0];
                Long sum = ((Number) row[1]).longValue();

                if (category != null) {
                    if (category.getFlow() == TransactionCategory.FlowType.OUTCOME
                            || category.getFlow() == TransactionCategory.FlowType.BOTH) {
                        categoryTotals.put(category.getDisplayName(), sum);
                    }
                } else {
                    log.warn("Unknown category from DB (null)");
                    categoryTotals.put("기타", sum);
                }
            }

            ConsumptionStatsResponse response = ConsumptionStatsResponse.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .categoryTotals(categoryTotals)
                    .build();

            return ResponseEntity.ok(new ApiResponse<>(
                    HttpStatus.OK.getReasonPhrase(),
                    ApiResponseMessage.CONSUMPTION_DATA_LOAD_SUCCESS.getMessage(),
                    response
            ));

        } catch (Exception e) {
            log.error("Error while loading consumption stats: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            ApiResponseMessage.CONSUMPTION_DATA_LOAD_FAIL.getMessage(),
                            null
                    ));
        }
    }
}
