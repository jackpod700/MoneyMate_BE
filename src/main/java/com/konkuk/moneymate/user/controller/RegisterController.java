package com.konkuk.moneymate.user.controller;

import com.konkuk.moneymate.activities.entity.User;
import com.konkuk.moneymate.activities.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * <h3>Register Controller</h3>
 *
 * <li><b>/register :</b> 입력한 회원 정보를 받아서 최종 회원 등록 요청 </li>
 */

@AllArgsConstructor
@RestController
public class RegisterController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
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
        user.encodeBCryptPassword();

        try{
            /**
             * 중복 검사를 자체적으로 하지만, 흐름은 회원 가입 폼에서 제어합니다
             */
            if(userRepository.existsByUserId(user.getUserId())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("중복 id 입니다.");
            }

            userRepository.save(user);
            return ResponseEntity.ok("회원 가입이 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();

            //400 Bad Request
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("회원가입 중 오류가 발생했습니다: " + e.getMessage());
        }

    }

    @GetMapping("/user/check-id")
    public ResponseEntity<?> checkUserId(@RequestParam String userId) {
        try{

            Optional<User> user = userRepository.findByUserId(userId);
            if(user.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복 id 입니다.");
            } else {
                return ResponseEntity.ok(userId);
            }

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


}
