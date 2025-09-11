package org.brainstorm.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionTokenService {
    private Map<Long, String> userTokens = new ConcurrentHashMap<>();

    public String generateTokenForUser(Long userId) {
        String token = java.util.UUID.randomUUID().toString();
        userTokens.put(userId, token);
        return token;
    }

    public boolean validateToken(Long userId, String token) {
        return token != null && token.equals(userTokens.get(userId));
    }
}
