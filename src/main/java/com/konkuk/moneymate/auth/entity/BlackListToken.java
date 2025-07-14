package com.konkuk.moneymate.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Getter
@Entity
@Table(name="token_blacklist")
public class BlackListToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 일반적으로 refreshToken만 저장합니다
    @Column(name = "invalid_access_token")
    private String invalidAccessToken;

    @Column(name = "invalid_refresh_token")
    private String invalidRefreshToken;

    public BlackListToken(String invalidRefreshToken) {
        this.invalidRefreshToken = invalidRefreshToken;
    }
}