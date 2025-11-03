package com.konkuk.moneymate.auth.application;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * <h3>AuthEntryPoint</h3>
 * <p>인증 실패 시 커스텀 401 응답을 반환하는 엔트리 포인트</p>
 * <li><b>역할:</b> 인증되지 않은 요청에 대한 JSON 응답 생성</li>
 * <li><b>응답 형식:</b> application/json</li>
 * <li><b>HTTP 상태:</b> 401 Unauthorized</li>
 * <li><b>인코딩:</b> UTF-8</li>
 */
@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * <h3>인증 실패 처리</h3>
     * <p>인증되지 않은 사용자의 요청에 대해 JSON 형식의 401 응답을 반환합니다</p>
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authException 인증 예외
     * @throws IOException IO 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        PrintWriter writer = response.getWriter();
        writer.println("{\"error\": \"" + authException.getMessage() + "\"}");
    }
}
