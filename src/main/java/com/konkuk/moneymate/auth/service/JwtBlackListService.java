package com.konkuk.moneymate.auth.service;

import com.konkuk.moneymate.auth.exception.InvalidTokenException;
import com.konkuk.moneymate.common.ApiResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <h3>JwtBlackListService</h3> token BLACKLIST 관리 서비스 <br>
 * RDBMS(MySQL) Table 에서 Redis cache 기반 저장위치 변경 <br>
 * token의 expiration이 지나면 삭제됩니다.
 * <li><b>blacklistAccessToken : </b> access token BLACKLIST 등록</li>
 * <li><b>blacklistRefreshToken : </b> refresh token BLACKLIST 등록</li>
 * <li><b>isAccessTokenBlacklisted : </b> BLACKLIST 검증 확인 </li>
 * <li><b>isRefreshTokenBlacklisted : </b> BLACKLIST 검증 확인  </li>
 * <li><b>validateAccessTokenNotBlacklisted : </b> BLACKLIST 검증</li>
 *
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class JwtBlackListService {
    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * <h3>Redis Key prefix</h3>
     */
    private static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "blacklist:access:";
    private static final String REFRESH_TOKEN_BLACKLIST_PREFIX = "blacklist:refresh:";


    public void blacklistAccessToken(String accessToken, long expirationMillis) {
        String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        String key = ACCESS_TOKEN_BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "true", expirationMillis, TimeUnit.MILLISECONDS);
        log.info("Access Token BLACKLIST registered: {} (TTL={}ms)", key, expirationMillis);
    }

    public void blacklistAccessToken(String accessToken) {
        String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        long expirationMillis = jwtService.getExpiration(token).getTime() - System.currentTimeMillis();
        if (expirationMillis > 0) {
            String key = ACCESS_TOKEN_BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key, "true", expirationMillis, TimeUnit.MILLISECONDS);
            log.info("Access Token BLACKLIST registered (TTL): {} (TTL={}ms)", key, expirationMillis);
        }
    }

    public void blacklistRefreshToken(String refreshToken, long expirationMillis) {
        String key = REFRESH_TOKEN_BLACKLIST_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, "true", expirationMillis, TimeUnit.MILLISECONDS);
        log.info("Refresh Token BLACKLIST registered: {} (TTL={}ms)", key, expirationMillis);
    }

    public void blacklistRefreshToken(String refreshToken) {
        long expirationMillis = jwtService.getExpiration(refreshToken).getTime() - System.currentTimeMillis();
        if (expirationMillis > 0) {
            String key = REFRESH_TOKEN_BLACKLIST_PREFIX + refreshToken;
            redisTemplate.opsForValue().set(key, "true", expirationMillis, TimeUnit.MILLISECONDS);
            log.info("Refresh Token BLACKLIST registered(TTL): {} (TTL={}ms)", key, expirationMillis);
        }
    }

    public boolean isAccessTokenBlacklisted(String accessToken) {
        String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        String key = ACCESS_TOKEN_BLACKLIST_PREFIX + token;
        return redisTemplate.hasKey(key);
    }

    public boolean isRefreshTokenBlacklisted(String refreshToken) {
        String key = REFRESH_TOKEN_BLACKLIST_PREFIX + refreshToken;
        return redisTemplate.hasKey(key);
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