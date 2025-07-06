package com.konkuk.moneymate.auth.application;


import com.konkuk.moneymate.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public AuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // token 검증 및 사용자 가져오기
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(accessToken != null) {

            /**
             * Prefix 때문에 substring(7)로 변경했습니다
             */
            if (jwtService.isBlacklisted(accessToken.substring(7))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("ERROR : this token is expired.");
                return;
            }

            // token 검증 및 사용자 가져오기
            String user = jwtService.getAuthUser(request);
            // 인증하기
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }


        // try-catch 안에 넣고 AccessTokenExpiredException 대응하는 메서드 만들기
        filterChain.doFilter(request, response);



    }
}