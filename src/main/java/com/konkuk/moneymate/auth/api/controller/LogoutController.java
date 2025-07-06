package com.konkuk.moneymate.auth.api.controller;


import com.konkuk.moneymate.auth.service.JwtService;
import com.konkuk.moneymate.auth.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h3>Login Controller</h3>
 *
 * <li><b> /logout :</b> 로그아웃 요청을 받아서 토큰 만료 및 로그아웃 처리 </li>
 * <li><b> /jwt :</b> jwt payload 정보 출력 요청 api (실제 서비스에서 사용하지 않음) </li>
 */
@RequiredArgsConstructor
@RestController
public class LogoutController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final JwtService jwtService;
    private final LogoutService logoutService;


    /**
     * <h3>Post : /logout</h3>
     * @param request
     * @return ResponseEntity.status(HttpStatus. )
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        logoutService.logout(request);
        return ResponseEntity.ok("로그아웃 처리 완료");
    }


}
