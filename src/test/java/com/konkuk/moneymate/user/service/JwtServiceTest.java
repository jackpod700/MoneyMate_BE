package com.konkuk.moneymate.user.service;

import com.konkuk.moneymate.activities.entity.User;
import com.konkuk.moneymate.activities.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtServiceTest {
    `
    @Test
    public void testJwtContainsCorrectUid() {
        String userId = "testuser";
        UUID expectedUid = UUID.randomUUID();

        UserRepository mockRepo = Mockito.mock(UserRepository.class);
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUid(expectedUid);

        when(mockRepo.findByUserId(userId)).thenReturn(Optional.of(mockUser));

        JwtService jwtService = new JwtService(mockRepo);
        String token = jwtService.getToken(userId);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(JwtService.key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String payloadUid = claims.get("uid", String.class);
        String dbUid = expectedUid.toString();

        System.out.println("✅ DB UID:      " + dbUid);
        System.out.println("✅ JWT Payload UID: " + payloadUid);

        assertEquals(dbUid, payloadUid);
        assertEquals(userId, claims.getSubject());
    }
}
