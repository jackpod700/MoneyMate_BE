package com.konkuk.moneymate.activities.controller;

import com.konkuk.moneymate.activities.dto.AssetDto;
import com.konkuk.moneymate.activities.service.AssetService;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import com.konkuk.moneymate.user.service.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AssetController {

    private final JwtService jwtService;
    private final AssetService assetService;

    public AssetController(JwtService jwtService, AssetService assetService) {
        this.jwtService = jwtService;
        this.assetService = assetService;
    }

    @PostMapping("/asset/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body, HttpServletRequest request) {

        String userUid = jwtService.getUserUid(request);
        String assetName = body.get("assetName").toString();
        String assetType = body.get("assetType").toString();
        Long assetPrice = Long.parseLong(body.get("assetPrice").toString());
        AssetDto assetDto = new AssetDto(assetName, assetType, assetPrice);

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
}
