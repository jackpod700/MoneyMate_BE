package com.konkuk.moneymate.user.controller;

import com.konkuk.moneymate.activities.entity.User;
import com.konkuk.moneymate.activities.repository.UserRepository;
import com.konkuk.moneymate.user.service.JwtService;
import com.konkuk.moneymate.user.service.LogoutService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.konkuk.moneymate.user.controller.UserController.*;
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

}
