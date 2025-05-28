package com.konkuk.moneymate.user.controller;


import com.konkuk.moneymate.user.auth.UserCredentials;
import com.konkuk.moneymate.user.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * <h3>Login Controller</h3>
 *
 * <li><b> /login :</b> user id, pw를 받아서 로그인 요청을 하고 header에 jwt 반환 </li>
 * <li><b> /logout :</b> 로그아웃 요청을 받아서 토큰 만료 및 로그아웃 처리 </li>
 * <li><b> /jwt :</b> jwt payload 정보 출력 요청 api (실제 서비스에서 사용하지 않음) </li>
 */

@AllArgsConstructor
@RestController
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> getToken(@RequestBody UserCredentials credentials) {
        UsernamePasswordAuthenticationToken creds = new UsernamePasswordAuthenticationToken(credentials.userid(),credentials.password());

        Authentication auth = authenticationManager.authenticate(creds);
        String jwts = jwtService.getToken(auth.getName());

        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + jwts)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                .build();

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("토큰이 없습니다.");
        }

        String token = authHeader.substring(7);

        logger.info("==token before blacklistToken");
        jwtService.blacklistToken(token);
        logger.info("==token after blacklistToken");

        return ResponseEntity.ok("로그아웃 처리 완료");
    }

    //
    @GetMapping("/login")
    public String loginPage() {

        return "logout success?"; // 또는 redirect 혹은 View 반환
    }


    // jwt 테스트 핸들러
    @GetMapping("/jwt")
    public ResponseEntity<?> printTokenInfo(HttpServletRequest request) {
        try {
            Map<String, Object> payload = jwtService.payloadPrint(request);
            return ResponseEntity.ok().body(payload);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}