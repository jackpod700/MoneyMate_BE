package com.konkuk.moneymate.auth.api.controller;


import com.konkuk.moneymate.auth.api.response.AuthTokensResponse;
import com.konkuk.moneymate.auth.auth.UserCredentials;
import com.konkuk.moneymate.auth.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
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

@RequiredArgsConstructor
@RestController
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    /**
     * <h3> Post : /login </h3>
     * @param credentials
     * @return ResponseEntity.OK
     */
    @PostMapping("/login")
    public ResponseEntity<?> getToken(@RequestBody UserCredentials credentials) {
        UsernamePasswordAuthenticationToken creds = new UsernamePasswordAuthenticationToken(credentials.userid(),credentials.password());

        Authentication auth = authenticationManager.authenticate(creds);
        String accessToken = jwtService.getAccessToken(auth.getName());
        String refreshToken = jwtService.getRefreshToken(auth.getName());

        AuthTokensResponse tokenResponse = AuthTokensResponse.of(accessToken, refreshToken, "Bearer");

        return ResponseEntity.ok(tokenResponse);
    }


    /**
     * <h3>Get /login : redirect </h3>
     * 사용하지 않으므로 호출되지 않습니다
     * @return
     */
    @GetMapping("/login")
    public String loginPage() {

        return "logout success?"; // 또는 redirect 혹은 View 반환
    }

    /**
     * <h3>Get : /jwt </h3>
     * jwt payload에 있는 uuid를 출력합니다
     * @param request
     * @return
     */
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



/*

 @PostMapping("/login")
    public ResponseEntity<?> getToken(@RequestBody UserCredentials credentials) {
        UsernamePasswordAuthenticationToken creds = new UsernamePasswordAuthenticationToken(credentials.userid(),credentials.password());

        Authentication auth = authenticationManager.authenticate(creds);
        String jwts = jwtService.getToken(auth.getName());

        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + jwts)
                 .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                .body("200: User " + credentials.userid() +" login successful");
    }


 */