package com.konkuk.moneymate.auth.service;

import com.konkuk.moneymate.auth.entity.BlackListToken;
import com.konkuk.moneymate.auth.exception.UnAuthorizationException;
import com.konkuk.moneymate.auth.repository.BlackListTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtBlackListService {

    private final BlackListTokenRepository blackListTokenRepository;

    /**
     * 블랙리스트 토큰 여부 확인
     */
    public boolean isBlacklisted(String token) {
        return blackListTokenRepository.existsByInvalidRefreshToken(token);
    }

    /**
     * save
     */
    public void blacklist(String refreshToken) {
        BlackListToken blackListToken = new BlackListToken(refreshToken);
        blackListTokenRepository.save(blackListToken);
    }

    public void validateAccessTokenNotBlacklisted(String accessToken) {
        String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;

        if (isBlacklisted(token)) {
            throw new UnAuthorizationException("ERROR: This token is expired or blacklisted.");
        }
    }
}
