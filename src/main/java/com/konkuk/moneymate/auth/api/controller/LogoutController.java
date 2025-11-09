package com.konkuk.moneymate.auth.api.controller;


import com.konkuk.moneymate.auth.api.request.RefreshTokenBody;
import com.konkuk.moneymate.auth.api.service.JwtService;
import com.konkuk.moneymate.auth.api.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * <h3>LogoutController</h3>
 * <p>사용자 로그아웃 및 토큰 무효화를 처리하는 컨트롤러</p>
 * <li><b>POST /logout:</b> 로그아웃 처리 및 토큰 블랙리스트 등록</li>
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class LogoutController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final JwtService jwtService;
    private final LogoutService logoutService;


    /**
     * <h3>POST /logout</h3>
     * <p>사용자를 로그아웃하고 Access Token 및 Refresh Token을 블랙리스트에 등록합니다</p>
     * <li><b>step 1:</b> Authorization 헤더에서 Access Token 추출</li>
     * <li><b>step 2:</b> Request Body에서 Refresh Token 추출</li>
     * <li><b>step 3:</b> Refresh Token 검증</li>
     * <li><b>step 4:</b> 두 토큰을 Redis 블랙리스트에 등록</li>
     * @param refreshTokenBody Refresh Token을 포함한 요청 본문
     * @param request HTTP 요청 (Authorization 헤더 포함)
     * @return ResponseEntity 200 OK (로그아웃 성공) 또는 400/401/500 (실패)
     * @throws IOException IO 예외
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenBody refreshTokenBody, HttpServletRequest request) throws IOException {
        return logoutService.logout(refreshTokenBody, request);
    }


}
