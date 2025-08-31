package com.konkuk.moneymate.auth.service;

import com.konkuk.moneymate.auth.api.request.RefreshTokenBody;
import com.konkuk.moneymate.auth.api.response.AuthTokensResponse;
import com.konkuk.moneymate.auth.application.RefreshTokenValidator;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ReissueTokenManageService {

    private final JwtService jwtService;
    private final RefreshTokenValidator refreshTokenValidator;
    private final JwtBlackListService jwtBlackListService;

    public ResponseEntity<?> reissueToken(RefreshTokenBody refreshTokenBody, HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = refreshTokenBody.getRefreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        /**
         * 기존 access token도 BLACKLIST 하도록 추가해야함
         */
        try {
            refreshTokenValidator.validateToken(refreshToken);
            refreshTokenValidator.validateBlacklistedToken(refreshToken);

            String userId = jwtService.getUserIdFromRefreshToken(refreshToken);

            jwtBlackListService.blacklistRefreshToken(refreshToken);

            String newAccessToken = jwtService.getAccessToken(userId);
            String newRefreshToken = jwtService.getRefreshToken(userId);

            ResponseCookie cookie = ResponseCookie.from("REFRESH_TOKEN", newRefreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/user/reissue-token")
                    .sameSite("None")
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());

            AuthTokensResponse tokenResponse = AuthTokensResponse.of(newAccessToken, newRefreshToken, "Bearer");

            return ResponseEntity.ok(new ApiResponse<>(
                    HttpStatus.OK.getReasonPhrase(),
                    ApiResponseMessage.TOKEN_REISSUE_SUCCESS.getMessage(),
                    tokenResponse
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("UNAUTHORIZED", e.getMessage(), "[401] 인증 실패"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("SERVER_ERROR", "서버 에러가 발생했습니다.", null));
        }
    }

}

/*
public AuthTokensResponse reissueToken(final String refreshToken, HttpServletRequest request) {

        // 검증 부분
        // refreshTokenValidator.validateToken(refreshToken);
        // refreshTokenValidator.validateLogoutToken(refreshToken);

        // Access Token에서 uid parse 하는 부분

        String userId = jwtService.getUserIdFromRefreshToken(refreshToken);
        // String uid = jwtService.getUserUid(request);

        String newAccessToken = jwtService.getAccessToken(userId);
        String newRefreshToken = jwtService.getRefreshToken(userId);

        return AuthTokensResponse.of(newAccessToken, newRefreshToken, "Bearer");
    }
 */
