package com.konkuk.moneymate.auth.repository;

import com.konkuk.moneymate.auth.entity.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListTokenRepository extends JpaRepository<BlackListToken, Long> {
    boolean existsByInvalidAccessToken(String accessToken);
    boolean existsByInvalidRefreshToken(String refreshToken);
}
