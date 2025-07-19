package com.konkuk.moneymate.auth.service;

import com.konkuk.moneymate.auth.api.request.RefreshTokenBody;
import com.konkuk.moneymate.auth.application.RefreshTokenValidator;
import com.konkuk.moneymate.auth.exception.InvalidTokenException;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

/**
 * <h3>LogoutService</h3>
 */
@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class LogoutService {
    private final JwtService jwtService;
    private final RefreshTokenValidator refreshTokenValidator;
    private final JwtBlackListService jwtBlackListService;

    public ResponseEntity<?> logout(RefreshTokenBody refreshTokenBody, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        System.out.println("/logout");

        String accessTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshToken = refreshTokenBody.getRefreshToken();
        // String refreshToken = request.getHeader("refresh");

        log.info("== accessTokenHeader : {}", accessTokenHeader);
        if (refreshToken == null || accessTokenHeader == null || !accessTokenHeader.startsWith("Bearer ")) {
            response.put("error", "invalid_request");
            response.put("message", "Access Token 또는 Refresh Token이 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            String accessToken = accessTokenHeader.substring(7);
            String userId = jwtService.getUserIdFromRefreshToken(refreshToken);

            refreshTokenValidator.validateToken(refreshToken);
            refreshTokenValidator.validateTokenOwner(refreshToken, userId);
            refreshTokenValidator.validateBlacklistedToken(refreshToken);

            log.info("== accessToken : {}", accessToken);

            jwtBlackListService.blacklistAccessToken(accessToken);
            jwtBlackListService.blacklistRefreshToken(refreshToken);

            response.put("message", "[200] 로그아웃 처리 완료");
            return ResponseEntity.ok(new ApiResponse<>(
                    HttpStatus.OK.getReasonPhrase(),
                    ApiResponseMessage.USER_LOGOUT_SUCCESS.getMessage(),
                    response
            ));

        } catch (InvalidTokenException e) {
            response.put("error", "[401] invalid_token");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    ApiResponseMessage.INVALID_TOKEN.getMessage(),
                    response
            ));

        } catch (Exception e) {
            response.put("error", "[500] server_error");
            response.put("message", "Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    ApiResponseMessage.INTERNAL_SERVER_ERROR.getMessage(),
                    response
            ));
        }
    }


}