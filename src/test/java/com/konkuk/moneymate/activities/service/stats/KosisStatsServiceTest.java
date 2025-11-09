package com.konkuk.moneymate.activities.service.stats;

import com.konkuk.moneymate.activities.stats.dto.KosisStatsDataRequest;
import com.konkuk.moneymate.activities.stats.dto.KosisStatsDataResponse;
import com.konkuk.moneymate.activities.stats.service.KosisStatsService;
import com.konkuk.moneymate.activities.stats.entity.KosisStatsAssetData;
import com.konkuk.moneymate.activities.stats.entity.KosisStatsIncomeData;
import com.konkuk.moneymate.activities.user.repository.UserRepository;
import com.konkuk.moneymate.activities.stats.repository.KosisStatsAssetDataRepository;
import com.konkuk.moneymate.activities.stats.repository.KosisStatsConsumptionDataRepository;
import com.konkuk.moneymate.activities.stats.repository.KosisStatsIncomeDataRepository;
import com.konkuk.moneymate.auth.service.JwtService;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class KosisStatsServiceTest {

    @Mock
    private KosisStatsAssetDataRepository kosisStatsAssetDataRepository;
    @Mock
    private KosisStatsIncomeDataRepository kosisStatsIncomeDataRepository;
    @Mock
    private KosisStatsConsumptionDataRepository kosisStatsConsumptionDataRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private KosisStatsService kosisStatsService;

    @Test
    void searchAssetData_ReturnsData_ForAssetsAndLiabilities() {
        // given
        KosisStatsDataRequest request = new KosisStatsDataRequest(35, "ignored", "ignored", 2023); // c2, itemId는 무시됨
        when(jwtService.getUserUid(any())).thenReturn("test-uid");

        List<KosisStatsAssetData> mockEntities = List.of(
                KosisStatsAssetData.builder()
                        .c1("B023").c2("C05").c1Name("30~39세").c2Name("자산")
                        .itemId("T01").itemName("전가구 평균").unitName("만원")
                        .year(2023).value(38617.0440717512).build(),

                KosisStatsAssetData.builder()
                        .c1("B023").c2("C05").c1Name("30~39세").c2Name("자산")
                        .itemId("T02").itemName("전가구 중앙값").unitName("만원")
                        .year(2023).value((double) 28060).build(),

                KosisStatsAssetData.builder()
                        .c1("B023").c2("C06").c1Name("30~39세").c2Name("부채")
                        .itemId("T01").itemName("전가구 평균").unitName("만원")
                        .year(2023).value(11316.9655166434).build(),

                KosisStatsAssetData.builder()
                        .c1("B023").c2("C06").c1Name("30~39세").c2Name("부채")
                        .itemId("T02").itemName("전가구 중앙값").unitName("만원")
                        .year(2023).value((double) 10344).build()
        );

        when(kosisStatsAssetDataRepository.findByC1AndC2NameInAndItemIdInAndYear(
                eq("B023"),
                argThat(c2Names -> c2Names.containsAll(List.of("자산", "부채"))),
                argThat(itemIds -> itemIds.containsAll(List.of("T01", "T02", "T03"))),
                eq(2023)
        )).thenReturn(mockEntities);

        // when
        ResponseEntity<?> response = kosisStatsService.searchAssetData(request, httpServletRequest);

        log.info("[TEST] ResponseEntity = {}", response);

        @SuppressWarnings("unchecked")
        ApiResponse<List<KosisStatsDataResponse>> apiResponse =
                (ApiResponse<List<KosisStatsDataResponse>>) response.getBody();

        log.info("[TEST] ApiResponse = {}", apiResponse);
        log.info("[TEST] Data count = {}", apiResponse != null ? apiResponse.getData().size() : null);
        if (apiResponse != null) {
            apiResponse.getData().forEach(d ->
                    log.info("[TEST] Result -> age={}, c2={}, item={}, value={}",
                            d.getAge(), d.getC2(), d.getItem(), d.getValue()));
        }

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getData()).hasSize(4);

        // 자산 평균/중앙값 포함 여부
        assertThat(apiResponse.getData()).anyMatch(d ->
                d.getC2().equals("자산") && d.getItem().equals("전가구 평균") && d.getValue().equals(38617.0440717512)
        );
        assertThat(apiResponse.getData()).anyMatch(d ->
                d.getC2().equals("자산") && d.getItem().equals("전가구 중앙값") && d.getValue().equals((double) 28060)
        );

        // 부채 평균/중앙값 포함 여부
        assertThat(apiResponse.getData()).anyMatch(d ->
                d.getC2().equals("부채") && d.getItem().equals("전가구 평균") && d.getValue().equals(11316.9655166434)
        );
        assertThat(apiResponse.getData()).anyMatch(d ->
                d.getC2().equals("부채") && d.getItem().equals("전가구 중앙값") && d.getValue().equals((double) 10344)
        );
    }

    @Test
    void searchAssetData_ReturnsBadRequest_WhenNoEntitiesFound() {
        // given
        KosisStatsDataRequest request = new KosisStatsDataRequest(45, "ignored", "ignored", 2023);
        when(jwtService.getUserUid(any())).thenReturn("test-uid");
        when(kosisStatsAssetDataRepository.findByC1AndC2NameInAndItemIdInAndYear(
                eq("B024"), anyList(), anyList(), eq(2023))
        ).thenReturn(List.of());

        // when
        ResponseEntity<?> response = kosisStatsService.searchAssetData(request, httpServletRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        @SuppressWarnings("unchecked")
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();

        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getData()).isNull();
        assertThat(apiResponse.getMessage()).isEqualTo(
                ApiResponseMessage.STATS_DATA_LOAD_FAIL.getMessage()
        );
    }

    @Test
    void searchIncomeData_ReturnsData_WhenEntityExists() {
        KosisStatsDataRequest request = new KosisStatsDataRequest(25, "경상소득", "중앙값", 2024);
        KosisStatsIncomeData entity = KosisStatsIncomeData.builder()
                .c1("021")
                .c2("C10")
                .c1Name("20~29세")
                .c2Name("경상소득")
                .itemId("T10")
                .itemName("중앙값")
                .unitName("만원")
                .year(2024)
                .value("4047.0")
                .build();

        when(jwtService.getUserUid(any())).thenReturn("test-uid");
        when(kosisStatsIncomeDataRepository.findByC1AndC2NameAndItemIdAndYear("021", "경상소득", "T10", 2024))
                .thenReturn(List.of(entity));

        ResponseEntity<?> response = kosisStatsService.searchIncomeData(request, httpServletRequest);

        log.info("[TEST] ResponseEntity = {}", response);

        @SuppressWarnings("unchecked")
        ApiResponse<List<KosisStatsDataResponse>> apiResponse =
                (ApiResponse<List<KosisStatsDataResponse>>) response.getBody();

        log.info("[TEST] ApiResponse = {}", apiResponse);
        log.info("[TEST] Data = {}", apiResponse != null ? apiResponse.getData() : null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getData()).hasSize(1);
        assertThat(apiResponse.getData().get(0).getItem()).isEqualTo("중앙값");
        assertThat(apiResponse.getData().get(0).getUnitName()).isEqualTo("만원");
        assertThat(apiResponse.getData().get(0).getValue()).isEqualTo(4047.0);
    }

    @Test
    void searchIncomeData_ReturnsBadRequest_WhenNoEntity() {
        KosisStatsDataRequest request = new KosisStatsDataRequest(25, "경상소득", "중앙값", 2024);

        when(jwtService.getUserUid(any())).thenReturn("test-uid");
        when(kosisStatsIncomeDataRepository.findByC1AndC2NameAndItemIdAndYear("021", "경상소득", "T10", 2024))
                .thenReturn(List.of());

        ResponseEntity<?> response = kosisStatsService.searchIncomeData(request, httpServletRequest);

        log.info("[TEST] ResponseEntity = {}", response);

        @SuppressWarnings("unchecked")
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();

        log.info("[TEST] ApiResponse = {}", apiResponse);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getData()).isNull();
        assertThat(apiResponse.getMessage()).isEqualTo(
                ApiResponseMessage.STATS_DATA_LOAD_FAIL.getMessage()
        );
    }
}