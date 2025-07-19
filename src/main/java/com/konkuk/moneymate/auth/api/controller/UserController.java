package com.konkuk.moneymate.auth.api.controller;

import com.konkuk.moneymate.activities.dto.UserDto;
import com.konkuk.moneymate.activities.entity.User;
import com.konkuk.moneymate.activities.repository.UserRepository;
import com.konkuk.moneymate.auth.api.request.RefreshTokenBody;
import com.konkuk.moneymate.auth.api.request.UserAuthRequest;
import com.konkuk.moneymate.auth.service.JwtService;
import com.konkuk.moneymate.auth.service.LogoutService;
import com.konkuk.moneymate.auth.service.MessageAuthService;
import com.konkuk.moneymate.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * <h3>User Controller</h3>
 *
 * <li><b>DELETE /user/delete</b></li>
 * <li><b>POST /user/verify/pw </b></li>
 * <li><b>GET /user/info </b></li>
 * <li><b>PATCH /user/update </b></li>
 */

@RequiredArgsConstructor
@RestController
public class UserController {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final LogoutService logoutService;
    private final UserService userService;
    private final MessageAuthService messageAuthService;



    /**
     * <h3>DELETE /user/delete</h3>
     * @param request Tomcat 으로부터 전달받은 servlet request
     * @return ResponseEntity.status <br> 및 계정 삭제 처리
     */
    @DeleteMapping("/user/delete")
    public ResponseEntity<?> deleteUser(@RequestBody RefreshTokenBody refreshTokenBody, HttpServletRequest request) {
        return userService.deleteUser(refreshTokenBody, request);
    }





    /**
     * <h3>POST /user/verify/pw </h3>
     * @param request Tomcat 으로부터 전달받은 servlet request
     * @param userDto <b>userDto 에서 password만 입력합니다</b>
     * @return ResponseEntity.status
     */
    @PostMapping("/user/verify/pw")
    public ResponseEntity<?> verifyPw(HttpServletRequest request, @RequestBody UserDto userDto) {
        return userService.verifyPw(userDto, request);
    }





    /**
     * <h3>POST /user/find-id</h3>
     * @param userAuthRequest
     * @param request
     * @return
     */
    @PostMapping("/user/find-id")
    public ResponseEntity<?> findUserId(@RequestBody UserAuthRequest userAuthRequest, HttpServletRequest request) {
        return userService.findUserId(userAuthRequest, request);
    }


    /**
     * <h3>POST /user/verify/reset-pw</h3>
     * <p>비밀번호 재설정을 위한 요청</p>
     * @param userAuthRequest
     * @param request
     * @return
     */
    @PostMapping("/user/reset-pw")
    public ResponseEntity<?> resetPasswordRequest(@RequestBody UserAuthRequest userAuthRequest, HttpServletRequest request) {
        return userService.resetPasswordRequest(userAuthRequest, request);
    }






    /**
     * <h3>GET /user/info </h3>
     * @param request
     * @return ResponseEntity.status
     */
    @GetMapping("/user/info")
    public ResponseEntity<UserDto> getUserInfo(HttpServletRequest request) {
        try {
            String userId = jwtService.getAuthUser(request);

            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("cannot find user"));

            UserDto userDto = UserDto.builder()
                    .userId(user.getUserId())
                    .userName(user.getUserName())
                    .birthday(user.getBirthday())
                    .phoneNumber(user.getPhoneNumber())
                    .build();

            return ResponseEntity.ok(userDto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }





    /**
     * <h3>PATCH /user/update </h3>
     * @param request tomcat에 보내는 servlet request
     * @param userDto 업데이트 하고자 하는 정보만 보내도 됩니다
     * @return ResponseEntity.status
     */
    @PatchMapping("/user/update")
    public ResponseEntity<String> updateUser(HttpServletRequest request, @RequestBody UserDto userDto) {
        try {
            String userId = jwtService.getAuthUser(request);

            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("cannot find user"));

            if (userDto.getUserName() != null && !userDto.getUserName().isBlank()) {
                user.setUserName(userDto.getUserName());
            }

            if (userDto.getBirthday() != null) {
                user.setBirthday(userDto.getBirthday());
            }

            if (userDto.getPhoneNumber() != null && !userDto.getPhoneNumber().isBlank()) {
                user.setPhoneNumber(userDto.getPhoneNumber());
            }

            userRepository.save(user);

            return ResponseEntity.ok("200: User" + userId + "updated successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("500: Error updating user");
        }
    }











}
