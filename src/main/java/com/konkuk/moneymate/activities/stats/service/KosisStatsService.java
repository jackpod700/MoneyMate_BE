package com.konkuk.moneymate.activities.stats.service;

import com.konkuk.moneymate.activities.stats.dto.KosisStatsDataRequest;
import com.konkuk.moneymate.activities.stats.dto.KosisStatsDataResponse;
import com.konkuk.moneymate.activities.stats.entity.KosisStatsAssetData;
import com.konkuk.moneymate.activities.stats.entity.KosisStatsConsumptionData;
import com.konkuk.moneymate.activities.stats.entity.KosisStatsIncomeData;
import com.konkuk.moneymate.activities.user.repository.UserRepository;
import com.konkuk.moneymate.activities.stats.repository.KosisStatsAssetDataRepository;
import com.konkuk.moneymate.activities.stats.repository.KosisStatsConsumptionDataRepository;
import com.konkuk.moneymate.activities.stats.repository.KosisStatsIncomeDataRepository;
import com.konkuk.moneymate.auth.service.JwtService;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <h3>KosisStatsService</h3>
 * <b>또래 통계 조회</b>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class KosisStatsService {
    private final KosisStatsAssetDataRepository kosisStatsAssetDataRepository;
    private final KosisStatsIncomeDataRepository kosisStatsIncomeDataRepository;
    private final KosisStatsConsumptionDataRepository kosisStatsConsumptionDataRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    /**
     * <h3>searchAssetData</h3> 1. 자산 통계 조회
     * @param kosisStatsDataRequest : age, c2, itemId, year
     * @param httpServletRequest
     * @return ResponseEntity.ok (성공 시)
     */
    public ResponseEntity<?> searchAssetData(KosisStatsDataRequest kosisStatsDataRequest, HttpServletRequest httpServletRequest) {
        String userUid = jwtService.getUserUid(httpServletRequest);
        Integer age = kosisStatsDataRequest.getAge();
        Integer year = kosisStatsDataRequest.getYear();

        Integer c = ageToC1(age);

        String c1 = switch (c) {
            case 20 -> "B022";
            case 30 -> "B023";
            case 40 -> "B024";
            case 50 -> "B025";
            case 60 -> "B026";
            default -> "B028";
        };


        List<String> itemIds = List.of("T01", "T02", "T03");
        List<String> c2Names = List.of("자산", "부채");

        List<KosisStatsAssetData> entityList =
                kosisStatsAssetDataRepository.findByC1AndC2NameInAndItemIdInAndYear(c1, c2Names, itemIds, year);

        if (entityList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    ApiResponseMessage.STATS_DATA_LOAD_FAIL.getMessage(),
                    null
            ));
        }

        List<KosisStatsDataResponse> responseList = entityList.stream()
                .map(e -> {
                    // itemId → 사람이 읽을 수 있는 이름 변환
                    String itemName = switch (e.getItemId()) {
                        case "T01" -> "평균";
                        case "T02" -> "중앙값";
                        case "T03" -> "비율";
                        default -> e.getItemName(); // fallback: DB 값 그대로 사용
                    };

                    return new KosisStatsDataResponse(
                            c,                      // 나이대 → 35세면 30
                            e.getC2Name(),          // "자산" 또는 "부채"
                            e.getItemName(),
                            itemName,               // 변환된 항목명
                            e.getYear().toString(), // 연도
                            e.getUnitName(),
                            (int) Double.parseDouble(e.getValue().toString())
                    );
                })
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.STATS_ASSET_DATA_LOAD_SUCCESS.getMessage(),
                responseList)
        );
    }









    /**
     * <h3>searchIncomeData</h3> 2. 소득 통계 조회
     * @param kosisStatsDataRequest : age, c2, itemId, year
     * @param httpServletRequest
     * @return ResponseEntity.ok (성공 시)<br>
     * <b>소득, 소비 통계 데이터의 경우 value가 String이라 확인 과정 필요함</b>
     */
    public ResponseEntity<?> searchIncomeData(KosisStatsDataRequest kosisStatsDataRequest, HttpServletRequest httpServletRequest) {
        String userUid = jwtService.getUserUid(httpServletRequest);
        Integer age = kosisStatsDataRequest.getAge();
        Integer year = kosisStatsDataRequest.getYear();

        Integer c = ageToC1(age);

        String c1 = switch (c) {
            case 20 -> "021";
            case 30 -> "022";
            case 40 -> "023";
            case 50 -> "024";
            case 60 -> "025";
            default -> "026";
        };

        List<String> itemIds = List.of("T00", "T10", "T20");
        List<String> c2Names = List.of("경상소득", "근로소득", "사업소득");

        List<KosisStatsIncomeData> entityList =
                kosisStatsIncomeDataRepository.findByC1AndC2NameInAndItemIdInAndYear(c1, c2Names, itemIds, year);

        if (entityList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    ApiResponseMessage.STATS_DATA_LOAD_FAIL.getMessage(),
                    null
            ));
        }

        List<KosisStatsDataResponse> responseList = entityList.stream()
                .map(e -> {
                    String itemName = switch (e.getItemId()) {
                        case "T00" -> "평균";
                        case "T10" -> "중앙값";
                        case "T20" -> "비율";
                        default -> e.getItemName();
                    };

                    int safeValue;
                    String raw = e.getValue();

                    if (raw == null || raw.isBlank() || raw.equals("-")) {
                        safeValue = 0;
                    } else {
                        try {
                            safeValue = (int) Double.parseDouble(raw); // 소수점은 버림
                        } catch (NumberFormatException ex) {
                            safeValue = 0;
                        }
                    }

                    return new KosisStatsDataResponse(
                            c,                      // 25세면 20
                            e.getC2Name(),
                            e.getItemName(),
                            itemName,
                            e.getYear().toString(),
                            e.getUnitName(),
                            safeValue
                    );
                })
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.STATS_INCOME_DATA_LOAD_SUCCESS.getMessage(),
                responseList)
        );
    }










    /**
     * <h3>searchConsumptionData</h3> 3. 소비 통계 조회
     * @param kosisStatsDataRequest : age, c2, itemId, year
     * @param httpServletRequest
     * @return ResponseEntity.ok (성공 시)
     */
    public ResponseEntity<?> searchConsumptionData(KosisStatsDataRequest kosisStatsDataRequest, HttpServletRequest httpServletRequest) {
        String userUid = jwtService.getUserUid(httpServletRequest);
        Integer age = kosisStatsDataRequest.getAge();
        Integer year = kosisStatsDataRequest.getYear();

        Integer c = ageToC1(age);

        String c1 = switch (c) {
            case 20 -> "021";
            case 30 -> "022";
            case 40 -> "023";
            case 50 -> "024";
            case 60 -> "025";
            default -> "026";
        };

        List<String> itemIds = List.of("T00", "T10", "T20");
        List<String> c2Names = List.of("소비지출", "식료품", "주거비", "교육비", "의료비", "교통비", "통신비", "기타지출");

        List<KosisStatsConsumptionData> entityList =
                kosisStatsConsumptionDataRepository.findByC1AndC2NameInAndItemIdInAndYear(c1, c2Names, itemIds, year);

        if (entityList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    ApiResponseMessage.STATS_DATA_LOAD_FAIL.getMessage(),
                    null
            ));
        }

        List<KosisStatsDataResponse> responseList = entityList.stream()
                .map(e -> {
                    String itemName = switch (e.getItemId()) {
                        case "T00" -> "평균";
                        case "T10" -> "중앙값";
                        case "T20" -> "비율";
                        default -> e.getItemName();
                    };

                    int safeValue;
                    String raw = e.getValue();

                    if (raw == null || raw.isBlank() || raw.equals("-")) {
                        safeValue = 0;
                    } else {
                        try {
                            safeValue = (int) Double.parseDouble(raw);
                        } catch (NumberFormatException ex) {
                            safeValue = 0;
                        }
                    }

                    return new KosisStatsDataResponse(
                            c,
                            e.getC2Name(),
                            e.getItemName(),
                            itemName,
                            e.getYear().toString(),
                            e.getUnitName(),
                            safeValue
                    );
                })
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.STATS_CONSUMPTION_DATA_LOAD_SUCCESS.getMessage(),
                responseList)
        );
    }


    /**
     * <h3>ageToC1</h3>
     * age 를 c1 카테고리로 변환
     * @param age 예시 25
     * @return (age - (age % 10)) - 예시 20
     */

    private Integer ageToC1(Integer age) {
        return (age - (age % 10));
    }

    /**
     * value(Object)가 숫자/문자열 상관없이 Double로 변환
     */
    private Double toDouble(Object value) {
        if (value instanceof Number num) {
            return num.doubleValue();
        }
        if (value instanceof String str) {
            try {
                return Double.parseDouble(str);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}

/*


 */
