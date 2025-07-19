package com.konkuk.moneymate.auth.service;

import com.konkuk.moneymate.activities.entity.User;
import com.konkuk.moneymate.activities.repository.UserRepository;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class RegisterService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<?> register(User user) {
        user.encodeBCryptPassword();

        try{
            /**
             * 중복 검사를 자체적으로 하지만, 흐름은 회원 가입 폼에서 제어합니다
             */
            if(userRepository.existsByUserId(user.getUserId())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        ApiResponseMessage.USER_REGISTER_FAIL.getMessage(),
                        "[400] BAD REQUEST"
                ));
            }

            userRepository.save(user);
            return ResponseEntity.ok(new ApiResponse<>(
                    HttpStatus.OK.getReasonPhrase(),
                    ApiResponseMessage.USER_LOGIN_SUCCESS.getMessage(),
                    "[200] 회원가입 성공"
            ));
        } catch (Exception e) {
            e.printStackTrace();

            //400 Bad Request
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            ApiResponseMessage.USER_REGISTER_FAIL.getMessage(),
                            "[400] BAD REQUEST"
                    ));
        }
    }
}
