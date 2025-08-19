package com.konkuk.moneymate.activities.controller;

import com.konkuk.moneymate.activities.dto.AssetDto;
import com.konkuk.moneymate.activities.dto.StockHoldingDto;
import com.konkuk.moneymate.activities.dto.StockHoldingResponseDto;
import com.konkuk.moneymate.activities.service.AssetService;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import com.konkuk.moneymate.auth.service.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h3>AssetController : 자산 api 관리 클래스</h3>
 * <li><b> POST /asset/register : 자산 등록 </b></li>
 * <li><b> GET /asset/get : 본인의 자산 조회 </b></li>
 * <li><b> POST /asset/delete : 자산 삭제</b></li>
 * <li><b> GET /asset/total-price : 자산 전체 가격 제공</b></li>
 */
@RequiredArgsConstructor
@RestController
public class AssetController {

    private final JwtService jwtService;
    private final AssetService assetService;

    @PostMapping("/asset/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body, HttpServletRequest request) {

        String userUid = jwtService.getUserUid(request);
        String assetName = body.get("assetName").toString();
        Long assetPrice = Long.parseLong(body.get("assetPrice").toString());
        AssetDto assetDto = new AssetDto(assetName,assetPrice);

        try {
            assetService.registerAsset(assetDto, userUid);
        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.getReasonPhrase(),
                            e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            ApiResponseMessage.INTERNAL_SERVER_ERROR.getMessage()));
        }

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.ASSET_REGISTER_SUCCESS.getMessage(), assetDto));
    }

    @GetMapping("/asset/get")
    public ResponseEntity<?> get(HttpServletRequest request) {
        String userUid = jwtService.getUserUid(request);

        List<AssetDto> assets = assetService.getAsset(userUid);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("asset", assets);

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                        ApiResponseMessage.ASSET_QUERY_SUCCESS.getMessage(), responseData));

    }

    @PostMapping("/asset/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String userUid = jwtService.getUserUid(request);
        String assetUid = body.get("assetUid").toString();
        try {
            assetService.deleteAsset(assetUid, userUid);
        }catch( EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.getReasonPhrase(),
                            e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            e.getMessage()));
        } catch(IllegalAccessException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(HttpStatus.FORBIDDEN.getReasonPhrase(),
                            ApiResponseMessage.NO_ACCESS_AUTHORITY.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            ApiResponseMessage.INTERNAL_SERVER_ERROR.getMessage()));
        }
        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                        ApiResponseMessage.ASSET_DELETE_SUCCESS.getMessage()));

    }

    @GetMapping("/asset/total-price")
    public ResponseEntity<?> getTotalPrice(HttpServletRequest request) {
        String userUid = jwtService.getUserUid(request);
        Long totalPrice = assetService.getTotalPrice(userUid);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.ASSET_TOTAL_SUCCESS.getMessage(), totalPrice));
    }

    @GetMapping("/asset/stock")
    public ResponseEntity<?> getStock(HttpServletRequest request) {
        String userUid = jwtService.getUserUid(request);
        if(userUid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                            ApiResponseMessage.USER_NOT_FOUND.getMessage()));
        }

        List<StockHoldingDto> stockAssets;
        try{
            stockAssets = assetService.getStockHoldingsWithPrice(userUid);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            ApiResponseMessage.INTERNAL_SERVER_ERROR.getMessage()));
        }

        // DTO 리스트를 응답용 DTO 리스트로 변환
        List<StockHoldingResponseDto> responseAssets = stockAssets.stream()
                .map(holding -> new StockHoldingResponseDto(
                        holding.getAccountName(),
                        holding.getStockName(),
                        holding.getTicker(),
                        holding.getQuantity().toString(),
                        holding.getCurrentTotalPrice().toString(), // 필드명을 'totalPrice'로 변경
                        holding.getProfit().toString()))
                .toList();

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("stockAssets", responseAssets); // 변환된 리스트를 담기

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.getReasonPhrase(),
                        ApiResponseMessage.STOCK_ASSET_QUERY_SUCCESS.getMessage(), responseData));
    }
}
