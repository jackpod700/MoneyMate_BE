package com.konkuk.moneymate.auth.api.service;

import com.konkuk.moneymate.activities.user.entity.User;
import com.konkuk.moneymate.activities.user.repository.UserRepository;
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

import java.util.Optional;

/**
 * <h3>RegisterService</h3>
 * <p>사용자 회원가입 및 아이디 중복 확인을 처리하는 서비스</p>
 * <li><b>회원가입:</b> 사용자 정보를 데이터베이스에 저장</li>
 * <li><b>비밀번호 암호화:</b> BCrypt를 사용한 패스워드 해싱</li>
 * <li><b>중복 검사:</b> 아이디 중복 여부 확인</li>
 */
@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class RegisterService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * <h3>회원가입</h3>
     * <p>신규 사용자를 등록합니다</p>
     * <li><b>1단계:</b> 비밀번호 BCrypt 암호화</li>
     * <li><b>2단계:</b> 아이디 중복 검사</li>
     * <li><b>3단계:</b> 사용자 정보 데이터베이스 저장</li>
     * @param user 회원가입 정보 (userId, userName, password, phoneNumber, birthday)
     * @return ResponseEntity 200 OK (성공) 또는 400/409 (실패)
     */
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


    public ResponseEntity<?> checkUserId(String userId) {
        try{
            Optional<User> user = userRepository.findByUserId(userId);
            if(user.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        ApiResponseMessage.USER_ID_EXISTS.getMessage(),
                        "[409] userId 중복"
                ));
            } else {
                return ResponseEntity.ok(new ApiResponse<>(
                        HttpStatus.OK.getReasonPhrase(),
                        ApiResponseMessage.USER_ID_AVAILABLE.getMessage(),
                        userId
                ));
            }

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    ApiResponseMessage.BAD_REQUEST.getMessage(),
                    null
            ));
        }
    }
}
