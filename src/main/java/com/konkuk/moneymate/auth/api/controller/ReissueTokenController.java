package com.konkuk.moneymate.auth.api.controller;

import
        static org.springframework.boot.web.server.Cookie.SameSite.NONE;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

import com.konkuk.moneymate.auth.api.request.RefreshTokenBody;
import com.konkuk.moneymate.auth.api.response.AuthTokensResponse;
import com.konkuk.moneymate.auth.service.ReissueTokenManageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <h3>ReissueTokenController</h3>
 * <p>JWT 토큰 재발급을 처리하는 컨트롤러</p>
 * <li><b>POST /user/reissue-token:</b> Refresh Token을 사용하여 새로운 Access Token 및 Refresh Token 발급</li>
 */
@RequiredArgsConstructor
@RestController
public class ReissueTokenController {

    private final ReissueTokenManageService reissueTokenManageService;

    /**
     * <h3>POST /user/reissue-token</h3>
     * <p>Refresh Token을 검증하고 새로운 토큰 쌍을 발급합니다</p>
     * <li><b>1단계:</b> Refresh Token 유효성 검증</li>
     * <li><b>2단계:</b> 블랙리스트 확인</li>
     * <li><b>3단계:</b> 기존 Refresh Token 블랙리스트 등록</li>
     * <li><b>4단계:</b> 새로운 Access Token 및 Refresh Token 생성</li>
     * @param refreshTokenBody Refresh Token을 포함한 요청 본문
     * @param request HTTP 요청
     * @param response HTTP 응답 (Cookie 설정용)
     * @return ResponseEntity 200 OK (새 토큰 포함) 또는 401 (실패)
     */
    @PostMapping("/user/reissue-token")
    public ResponseEntity<?> reissueToken(@RequestBody RefreshTokenBody refreshTokenBody,
            HttpServletRequest request,
            HttpServletResponse response) {

        return reissueTokenManageService.reissueToken(refreshTokenBody, request, response);
    }
}


/*
@PostMapping("/user/reissue-token")
    public ResponseEntity<AuthTokensResponse> reissueToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = request.getHeader("Refresh");

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        AuthTokensResponse tokenResponse = reissueTokenManageService.reissueToken(refreshToken, request);

        ResponseCookie cookie = ResponseCookie.from("REFRESH_TOKEN", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/user/reissue-token")
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(tokenResponse);
    }
 */