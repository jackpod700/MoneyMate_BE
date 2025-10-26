package com.konkuk.moneymate.auth.service;

import com.konkuk.moneymate.activities.dto.UserDto;
import com.konkuk.moneymate.activities.entity.User;
import com.konkuk.moneymate.activities.repository.UserRepository;
import com.konkuk.moneymate.auth.api.request.RefreshTokenBody;
import com.konkuk.moneymate.auth.api.request.UserAuthRequest;
import com.konkuk.moneymate.common.ApiResponse;
import com.konkuk.moneymate.common.ApiResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final LogoutService logoutService;
    private final RedisTemplate<String, String> redisTemplate;

    public ResponseEntity<?> deleteUser(RefreshTokenBody refreshTokenBody, HttpServletRequest request){
        try{
            String userId = jwtService.getAuthUser(request);

            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("cannot find user"));

            userRepository.delete(user);
            logoutService.logout(refreshTokenBody, request);

            return ResponseEntity.ok(new ApiResponse<>(
                    HttpStatus.OK.getReasonPhrase(),
                    ApiResponseMessage.USER_DELETE_SUCCESS.getMessage(),
                    "[200] User " + userId + " deleted"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            ApiResponseMessage.BAD_REQUEST.getMessage(),
                            "[400] BAD REQUEST"
                    ));
        }
    }


    public ResponseEntity<?> verifyPw(UserDto userDto, HttpServletRequest request) {
        String userId = jwtService.getAuthUser(request);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("cannot find user"));

        String password = userDto.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pw = user.getPassword();

        if(encoder.matches(password, pw)){
            return ResponseEntity.ok(new ApiResponse<>(
                    HttpStatus.OK.getReasonPhrase(),
                    ApiResponseMessage.USER_ID_PW_VERIFY_SUCCESS.getMessage(),
                    "[200] Password verified"
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ApiResponseMessage.USER_ID_PW_VERIFY_FAIL.getMessage(),
                "[401] Verify failed"
        ));
    }



    public ResponseEntity<?> findUserId(UserAuthRequest userAuthRequest, HttpServletRequest request) {
        String ph = userAuthRequest.getPhoneNumber();
        String userVerifyCode = userAuthRequest.getUserVerifyCode();


        // 다시 010-0000-0000 포맷으로 변경 후 조회해야 합니다
        String phoneNumber = formatPhoneNumber(ph);

        String savedCode = redisTemplate.opsForValue().get(ph);
        log.info("savedCode: {}", savedCode);
        log.info("phoneNumber: {}", phoneNumber);

        if (savedCode == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    "EXPIRED",
                    "인증코드가 존재하지 않거나 만료되었습니다.",
                    null
            ));
        }

        if (!savedCode.equals(userVerifyCode)) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    "MISMATCH",
                    "인증코드가 일치하지 않습니다.",
                    null
            ));
        }

        redisTemplate.delete(ph);

        Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    HttpStatus.NOT_FOUND.getReasonPhrase(),
                    "해당 전화번호로 가입된 사용자가 없습니다.",
                    null
            ));
        }

        String userId = userOptional.get().getUserId();

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.getReasonPhrase(),
                ApiResponseMessage.USER_ID_FIND_SUCCESS.getMessage(),
                userId
        ));
    }


    /**
     * <h3>changePassword</h3>
     * @param userAuthRequest
     * @param request
     * @return
     */
    public ResponseEntity<?> changePassword(UserAuthRequest userAuthRequest, HttpServletRequest request){
        String userId = userAuthRequest.getUserId();
        String newPassword = userAuthRequest.getPassword();

        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Password cannot be empty.",
                    "[400] "
            ));
        }

        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    HttpStatus.NOT_FOUND.getReasonPhrase(),
                    "해당하는 사용자를 찾을 수 없습니다.",
                    null
            ));
        }


        User user = userOptional.get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(newPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "이전 비밀번호와 동일합니다.",
                    null
            ));
        }

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);


        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.getReasonPhrase(),
                "비밀번호가 성공적으로 변경되었습니다.",
                null
        ));

    }

    public ResponseEntity<?> resetPasswordRequest(UserAuthRequest userAuthRequest, HttpServletRequest request) {
        String userId = userAuthRequest.getUserId();
        String ph = userAuthRequest.getPhoneNumber();
        String userVerifyCode = userAuthRequest.getUserVerifyCode();
        String newPassword = userAuthRequest.getPassword();

        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Password cannot be empty.",
                    "[400] "
            ));
        }

        // 다시 010-0000-0000 포맷으로 변경 후 조회해야 합니다
        String phoneNumber = formatPhoneNumber(ph);
        log.info("phoneNumber: {}", phoneNumber);

        String savedCode = redisTemplate.opsForValue().get(ph);
        log.info("savedCode: {}", savedCode);
        log.info("phoneNumber: {}", phoneNumber);

        if (savedCode == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "인증코드가 존재하지 않거나 만료되었습니다.",
                    "[400] Missmatch or expired"
            ));
        }

        if (!savedCode.equals(userVerifyCode)) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "인증코드가 존재하지 않거나 만료되었습니다.",
                    "[400] Missmatch or expired"
            ));
        }

        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    HttpStatus.NOT_FOUND.getReasonPhrase(),
                    "해당하는 사용자를 찾을 수 없습니다.",
                    null
            ));
        }

        User user = userOptional.get();
        if (!user.getPhoneNumber().equals(phoneNumber)) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    HttpStatus.NOT_FOUND.getReasonPhrase(),
                    "해당하는 사용자를 찾을 수 없습니다",
                    null
            ));
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(newPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "이전 비밀번호와 동일합니다.",
                    null
            ));
        }

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        // 인증코드 사용 후 삭제
        redisTemplate.delete(ph);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.getReasonPhrase(),
                "비밀번호가 성공적으로 변경되었습니다.",
                null
        ));
    }






    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 11) {
            return phoneNumber;
        }

        if (phoneNumber.startsWith("010") || phoneNumber.startsWith("070")) {
            // 3 - 4 - 4
            return phoneNumber.substring(0, 3) + "-" +
                    phoneNumber.substring(3, 7) + "-" +
                    phoneNumber.substring(7);
        }

        return phoneNumber; // 010, 070으로 시작하지 않으면 그대로 반환
    }

}
