package com.konkuk.moneymate.auth.service;

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

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
