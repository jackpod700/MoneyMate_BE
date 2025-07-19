package com.konkuk.moneymate.auth.application;

import com.konkuk.moneymate.auth.exception.InvalidTokenException;
import com.konkuk.moneymate.auth.exception.RefreshTokenExpiredException;
import com.konkuk.moneymate.auth.repository.BlackListTokenRepository;
import com.konkuk.moneymate.auth.service.JwtService;
import com.konkuk.moneymate.common.ApiResponseMessage;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Component
public class RefreshTokenValidator {
    private final BlackListTokenRepository blackListTokenRepository;
    private final JwtService jwtService;
    String userId;

    /**
     * 확인 1: 토큰 유효성 검사
     * - 서명 검증, 만료 검증
     */
    public void validateToken(String token) {
        try {
            jwtService.getUserIdFromRefreshToken(token);
        } catch (ExpiredJwtException e) {
            throw new RefreshTokenExpiredException(ApiResponseMessage.INVALID_REFRESH_TOKEN.getMessage());
        } catch (JwtException e) {
            throw new RefreshTokenExpiredException(ApiResponseMessage.INVALID_REFRESH_TOKEN.getMessage());
        }
    }

    /**
     * 확인 2: 토큰 소유자 일치 여부 검증
     */
    public void validateTokenOwner(String refreshToken, String id) {
        String userId = jwtService.getUserIdFromRefreshToken(refreshToken);

        if (!id.equals(userId)) {
            throw new RuntimeException(ApiResponseMessage.INVALID_REFRESH_TOKEN.getMessage());
        }
    }


    /**
     * 확인 3: 블랙리스트에 등록된 토큰인지 검증
     */
    public void validateBlacklistedToken(String refreshToken) {
        if (blackListTokenRepository.existsByInvalidRefreshToken(refreshToken)) {
            throw new RuntimeException(ApiResponseMessage.INVALID_REFRESH_TOKEN.getMessage());
        }
    }

}
