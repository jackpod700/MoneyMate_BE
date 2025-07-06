package com.konkuk.moneymate.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class LogoutService {
    private final JwtService jwtService;

    public ResponseEntity<?> logout(HttpServletRequest request) {
        System.out.println("/logout");
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("== authHeader : {}", authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("jwt: not found");
        }

        String token = authHeader.substring(7);
        log.info("== token : {}", token);
        log.info("== token before blacklistToken");
        jwtService.blacklistToken(token);
        log.info("== token after blacklistToken");

        return ResponseEntity.ok("로그아웃 처리 완료");
    }

}