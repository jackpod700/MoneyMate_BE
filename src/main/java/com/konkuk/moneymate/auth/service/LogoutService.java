package com.konkuk.moneymate.auth.service;

import com.konkuk.moneymate.auth.application.RefreshTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ResponseEntity<?> logout(HttpServletRequest request) {
        System.out.println("/logout");
        String accessTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshToken = request.getHeader("refresh");

        log.info("== accessTokenHeader : {}", accessTokenHeader);
        if (refreshToken == null || accessTokenHeader == null || !accessTokenHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("accessToken : not found");
        }

        String accessToken = accessTokenHeader.substring(7);
        String userId= jwtService.getUserIdFromRefreshToken(refreshToken);

        refreshTokenValidator.validateToken(refreshToken);
        refreshTokenValidator.validateTokenOwner(refreshToken, userId);
        refreshTokenValidator.validateBlacklistedToken(refreshToken);

        log.info("== accessToken : {}", accessToken);
        log.info("== token before blacklistToken");
        jwtService.blacklistToken(accessToken);
        jwtService.blacklistToken(refreshToken);
        log.info("== token after blacklistToken");


        return ResponseEntity.ok("로그아웃 처리 완료");
    }

}