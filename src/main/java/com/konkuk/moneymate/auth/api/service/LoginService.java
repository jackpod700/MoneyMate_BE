package com.konkuk.moneymate.auth.api.service;

import com.konkuk.moneymate.auth.api.controller.LoginController;
import com.konkuk.moneymate.auth.api.response.AuthTokensResponse;
import com.konkuk.moneymate.auth.application.UserCredentials;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h3>LoginService</h3>
 * <p>사용자 로그인 인증 및 JWT 토큰 발급을 처리하는 서비스</p>
 * <li><b>인증:</b> Spring Security AuthenticationManager를 통한 사용자 인증</li>
 * <li><b>토큰 발급:</b> 인증 성공 시 Access Token 및 Refresh Token 생성</li>
 * <li><b>응답 형식:</b> AuthTokensResponse DTO로 토큰 반환</li>
 */
@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * <h3>사용자 로그인</h3>
     * <p>사용자 인증을 수행하고 JWT 토큰을 발급합니다</p>
     * <li><b>1단계:</b> UsernamePasswordAuthenticationToken 생성</li>
     * <li><b>2단계:</b> AuthenticationManager로 인증 수행</li>
     * <li><b>3단계:</b> Access Token 및 Refresh Token 생성</li>
     * <li><b>4단계:</b> AuthTokensResponse로 토큰 반환</li>
     * @param credentials 사용자 인증 정보 (userid, password)
     * @return ResponseEntity 200 OK (토큰 포함) 또는 401 Unauthorized
     */
    public ResponseEntity<?> login(UserCredentials credentials) {
        UsernamePasswordAuthenticationToken creds = new UsernamePasswordAuthenticationToken(
                credentials.userid(), credentials.password()
        );

        try {
            Authentication auth = authenticationManager.authenticate(creds);

            String accessToken = jwtService.getAccessToken(auth.getName());
            String refreshToken = jwtService.getRefreshToken(auth.getName());

            AuthTokensResponse tokenResponse = AuthTokensResponse.of(accessToken, refreshToken, "Bearer");

            return ResponseEntity.ok(new ApiResponse<>(
                    HttpStatus.OK.getReasonPhrase(),
                    ApiResponseMessage.USER_LOGIN_SUCCESS.getMessage(),
                    tokenResponse
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    ApiResponseMessage.USER_LOGIN_FAIL.getMessage(),
                    null
            ));
        }
    }

}
