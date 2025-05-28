package com.konkuk.moneymate.user.service;
import com.konkuk.moneymate.activities.entity.User;
import com.konkuk.moneymate.activities.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Data
@Component
public class JwtService {
    static final long EXPIRATION_TIME = 3600000L;
    static final String PREFIX = "Bearer";
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();
    static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);


    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void blacklistToken(String token) {
        System.out.println(">>> blacklistToken() 호출됨");

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            long ttl = expiration.getTime() - System.currentTimeMillis();

            if (ttl > 0) {
                blacklist.put(token, expiration.getTime());
            }

            System.out.println("=== blacklist size ===" + blacklist.size());
            System.out.println("================== 블랙리스트 목록 ==================");
            blacklist.forEach((key, exp) -> System.out.println("토큰: " + key + ", 만료시간: " + exp));
            System.out.println("====================================================");

        } catch (Exception e) {
            System.out.println(">>> Exception ");
            e.printStackTrace();
        }
    }


    public boolean isBlacklisted(String token) {
        Long expiry = blacklist.get(token);
        if (expiry == null) return false;

        // 만료 시간 지난 블랙리스트 항목 제거
        if (expiry < System.currentTimeMillis()) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }

    public String getUserUid(HttpServletRequest request){
        Map<String, Object> payload = payloadPrint(request);
        return (String) payload.get("uid");
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