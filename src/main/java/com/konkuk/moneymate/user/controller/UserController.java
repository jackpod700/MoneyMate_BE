package com.konkuk.moneymate.user.controller;

import com.konkuk.moneymate.activities.dto.UserDto;
import com.konkuk.moneymate.activities.entity.User;
import com.konkuk.moneymate.activities.repository.UserRepository;
import com.konkuk.moneymate.user.service.JwtService;
import com.konkuk.moneymate.user.service.LogoutService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.konkuk.moneymate.user.controller.UserController.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * <h3>User Controller</h3>
 *
 * <li><b>/user/check-id :</b> 중복 유저 확인 절차 거쳐야 함</li>
 */

@RequiredArgsConstructor
@RestController
public class UserController {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final LogoutService logoutService;


    /**
     * <h3>DELETE /user/delete</h3>
     * @param request
     * @return
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
     * <h3>GET /user/info </h3>
     * @param request
     * @return
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
     * @param request
     * @param userDto
     * @return
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
