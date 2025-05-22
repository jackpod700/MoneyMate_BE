package com.konkuk.moneymate.user.service;
import com.konkuk.moneymate.activities.entity.User;
import com.konkuk.moneymate.activities.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtService {
    static final long EXPIRATION_TIME = 3600000L;
    static final String PREFIX = "Bearer";
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);


    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // uid도 받게 수정했습니다
    public String getToken(String userId) {
        UUID uid = userRepository.findByUserId(userId)
                .map(User::getUid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        logger.info("uid :: " + uid.toString());

        return Jwts.builder()
                .setSubject(userId)
                .claim("uid", uid.toString())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String getAuthUser(HttpServletRequest request){
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(token != null ){
            String user = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.replace(PREFIX, ""))
                    .getBody()
                    .getSubject();

            if(user != null){
                return user;
            }
        }
        return null;
    }


    public Map<String, Object> payloadPrint(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith(PREFIX)) {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.replace(PREFIX, "").trim())
                    .getBody();

            Map<String, Object> payload = new HashMap<>();
            payload.put("sub", claims.getSubject());
            payload.put("uid", claims.get("uid", String.class));
            payload.put("exp", claims.getExpiration());

            logger.info("body를 확인하세요");

            return payload;
        } else {
            throw new RuntimeException("유효한 JWT가 아니거나 Bearer prefix가 입력되었는지 확인하세요");
        }
    }
}