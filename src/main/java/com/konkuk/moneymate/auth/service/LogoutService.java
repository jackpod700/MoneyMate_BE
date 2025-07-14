package com.konkuk.moneymate.auth.service;

import com.konkuk.moneymate.auth.application.RefreshTokenValidator;
import com.konkuk.moneymate.auth.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        System.out.println("/logout");

        String accessTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshToken = request.getHeader("refresh");

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
            log.info("== token before blacklistToken");

            jwtBlackListService.blacklistAccessToken(accessToken);
            jwtBlackListService.blacklistRefreshToken(refreshToken);

            log.info("== token after blacklistToken");

            response.put("message", "logout 처리 완료");
            return ResponseEntity.ok(response);

        } catch (InvalidTokenException e) {
            response.put("error", "invalid_token");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (Exception e) {
            response.put("error", "server_error");
            response.put("message", "Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}