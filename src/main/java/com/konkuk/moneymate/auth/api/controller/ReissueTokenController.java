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

@RequiredArgsConstructor
@RestController
public class ReissueTokenController {

    private final ReissueTokenManageService reissueTokenManageService;

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