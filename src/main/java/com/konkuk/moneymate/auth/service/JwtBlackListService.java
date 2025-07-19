package com.konkuk.moneymate.auth.service;

import com.konkuk.moneymate.auth.entity.BlackListToken;
import com.konkuk.moneymate.auth.exception.InvalidTokenException;
import com.konkuk.moneymate.auth.exception.UnAuthorizationException;
import com.konkuk.moneymate.auth.repository.BlackListTokenRepository;
import com.konkuk.moneymate.common.ApiResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtBlackListService {

    private final BlackListTokenRepository blackListTokenRepository;

    public void blacklistAccessToken(String accessToken) {
        String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        blackListTokenRepository.save(BlackListToken.ofAccessToken(token));
    }

    public void blacklistRefreshToken(String refreshToken) {
        blackListTokenRepository.save(BlackListToken.ofRefreshToken(refreshToken));
    }

    public boolean isAccessTokenBlacklisted(String accessToken) {
        String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return blackListTokenRepository.existsByInvalidAccessToken(token);
    }

    public boolean isRefreshTokenBlacklisted(String refreshToken) {
        return blackListTokenRepository.existsByInvalidRefreshToken(refreshToken);
    }

    public void validateAccessTokenNotBlacklisted(String accessToken) {
        if (isAccessTokenBlacklisted(accessToken)) {
            throw new InvalidTokenException(ApiResponseMessage.INVALID_TOKEN.getMessage());
        }
    }

    public void validateRefreshTokenNotBlacklisted(String refreshToken) {
        if (isRefreshTokenBlacklisted(refreshToken)) {
            throw new InvalidTokenException(ApiResponseMessage.INVALID_TOKEN.getMessage());
        }
    }
}
