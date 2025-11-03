package com.konkuk.moneymate.auth.api.controller;

import com.konkuk.moneymate.activities.entity.User;
import com.konkuk.moneymate.activities.repository.UserRepository;
import com.konkuk.moneymate.auth.service.RegisterService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * <h3>RegisterController</h3>
 * <p>사용자 회원가입, 아이디 중복 확인 처리</p>
 * <li><b>POST /register:</b> 신규 사용자 등록</li>
 * <li><b>GET /user/check-id:</b> 아이디 중복 확인</li>
 */

@AllArgsConstructor
@RestController
public class RegisterController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RegisterService registerService;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    /**
     * <li><b>/register :</b> 입력한 회원 정보를 받아서 최종 회원 등록 요청 </li>
     * @param user : User instance를 json 객체로 받아서 To Entity  <br>
     *  <b>userId, username, password : </b> 사용자가 입력한 값으로 초기화 <br>
     *
     *  <li><b>/user/check-id :</b> 중복 유저 확인 절차 거쳐야 함</li>
     * @return
     *
     *
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return registerService.register(user);
    }


    @GetMapping("/user/check-id")
    public ResponseEntity<?> checkUserId(@RequestParam String userId) {
        return registerService.checkUserId(userId);
    }


}
