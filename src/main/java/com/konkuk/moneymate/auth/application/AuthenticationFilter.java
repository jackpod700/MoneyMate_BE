package com.konkuk.moneymate.auth.application;


import com.konkuk.moneymate.auth.exception.InvalidTokenException;
import com.konkuk.moneymate.auth.service.JwtBlackListService;
import com.konkuk.moneymate.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final JwtBlackListService jwtBlackListService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (accessToken != null) {
            try {
                jwtBlackListService.validateAccessTokenNotBlacklisted(accessToken);

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