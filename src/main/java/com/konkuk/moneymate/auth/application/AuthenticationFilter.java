package com.konkuk.moneymate.auth.application;


import com.konkuk.moneymate.auth.exception.InvalidTokenException;
import com.konkuk.moneymate.auth.api.service.JwtBlackListService;
import com.konkuk.moneymate.auth.api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * <h3>AuthenticationFilter</h3>
 * <p>JWT 토큰 검증을 수행하는 Spring Security Filter</p>
 * <li><b>토큰 추출:</b> Authorization 헤더에서 Bearer 토큰 추출</li>
 * <li><b>블랙리스트 검증:</b> 로그아웃된 토큰인지 확인</li>
 * <li><b>토큰 parsing:</b> JWT 서명 검증 및 사용자 정보 추출</li>
 * <li><b>인증 설정:</b> SecurityContext에 Authentication 객체 저장</li>
 * <li><b>예외 처리:</b> 토큰 검증 실패 시 401 응답 반환</li>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final JwtBlackListService jwtBlackListService;

    /**
     * <h3>JWT 토큰 검증 필터 로직</h3>
     * <p>모든 HTTP 요청에 대해 JWT 토큰을 검증합니다</p>
     * <li><b>1단계:</b> Authorization 헤더에서 토큰 추출</li>
     * <li><b>2단계:</b> 토큰이 블랙리스트에 있는지 확인</li>
     * <li><b>3단계:</b> JWT 파싱 및 사용자 정보 추출</li>
     * <li><b>4단계:</b> Authentication 객체 생성 및 SecurityContext 저장</li>
     * <li><b>5단계:</b> 다음 필터로 요청 전달</li>
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException IO 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (accessToken != null) {
            try {
                jwtBlackListService.validateAccessTokenNotBlacklisted(accessToken);
                log.info("Access token validated");

                // token 검증 시 발생하는 모든 예외 처리
                String user = jwtService.getAuthUser(request);
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());   
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (InvalidTokenException | io.jsonwebtoken.JwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"Unauthorized\",\"message\":\"" + e.getMessage() + "\", \"data\": { \"message\": \"[401] invalid token.\"}}");
                // "{\"error\":\"invalid_token\",\"message\":\"" + e.getMessage() + "\"}"
                return;
            } catch (Exception e) {
                // catch any unexpected error
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"Server Error\",\"message\":\"" + e.getMessage() + "\", \"data\": { \"message\": \"[401] invalid token.\"}}");


                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}