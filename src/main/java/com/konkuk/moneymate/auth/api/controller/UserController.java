package com.konkuk.moneymate.auth.api.controller;

import com.konkuk.moneymate.activities.dto.UserDto;
import com.konkuk.moneymate.activities.entity.User;
import com.konkuk.moneymate.activities.repository.UserRepository;
import com.konkuk.moneymate.auth.service.JwtService;
import com.konkuk.moneymate.auth.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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


    /**
     * <h3>DELETE /user/delete</h3>
     * @param request Tomcat 으로부터 전달받은 servlet request
     * @return ResponseEntity.status <br> 및 계정 삭제 처리
     */
    @DeleteMapping("/user/delete")
    public ResponseEntity<String> deleteUser(HttpServletRequest request) {
        try{
            String userId = jwtService.getAuthUser(request);

            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("cannot find user"));

            userRepository.delete(user);
            logoutService.logout(request);
            return ResponseEntity.ok("200: User " + userId + " deleted");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("500: Error deleting user");
        }
    }




    /**
     * <h3>POST /user/verify/pw </h3>
     * @param request Tomcat 으로부터 전달받은 servlet request
     * @param userDto <b>userDto 에서 password만 입력합니다</b>
     * @return ResponseEntity.status
     */
    @PostMapping("/user/verify/pw")
    public ResponseEntity<String> verifyPw(HttpServletRequest request, @RequestBody UserDto userDto) {
        String userId = jwtService.getAuthUser(request);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("cannot find user"));

        String password = userDto.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pw = user.getPassword();

        if(encoder.matches(password, pw)){
            return ResponseEntity.ok("200: Password verified");
        }

        return ResponseEntity.status(401).body("401: Password mismatch");
    }





    /**
     * <h3>GET /user/info </h3>
     * @param request Tomcat 으로부터 전달받은 servlet request
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



    /**
     * <h3>PATCH /user/reset-pw</h3>
     * @param request tomcat을 통해 전달받은 servlet request
     * @param userDto password를 입력받을 object
     * @return ResponseEntity.status
     */
    @PatchMapping("/user/reset-pw")
    public ResponseEntity<String> resetPassword(HttpServletRequest request, @RequestBody UserDto userDto) {
        try {
            String userId = jwtService.getAuthUser(request);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Exception: cannot find user"));

            String newPassword = userDto.getPassword();

            if (newPassword == null || newPassword.isEmpty()) {
                return ResponseEntity.status(400).body("400: Password cannot be empty");
            }

            if (encoder.matches(newPassword, user.getPassword())) {
                return ResponseEntity.status(400).body("400: new password is same as old password");
            }

            /// ##### jpa Entity 에는 setter를 사용하지 않는 것이 원칙이긴 합니다
            String pw = encoder.encode(newPassword);
            user.setPassword(pw);
            userRepository.save(user);

            return ResponseEntity.ok("200: Password changed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("500: Internal Server Error");
        }
    }



}
