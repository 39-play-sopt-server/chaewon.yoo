package org.sopt.auth.repository;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TokenStore {

    private final Map<String, LoginToken> tokens = new ConcurrentHashMap<>();

    public void save(String tokenId, Long memberId, Instant expiresAt) {
        Instant now = Instant.now();
        tokens.entrySet().removeIf(entry -> !entry.getValue().expiresAt().isAfter(now));
        tokens.put(tokenId, new LoginToken(memberId, expiresAt));
    }

    public boolean isActive(String tokenId, Long memberId) {
        LoginToken token = tokens.get(tokenId);
        if (token == null) {
            return false;
        }
        if (!token.expiresAt().isAfter(Instant.now())) {
            tokens.remove(tokenId, token);
            return false;
        }
        return token.memberId().equals(memberId);
    }

    public void delete(String tokenId) {
        tokens.remove(tokenId);
    }

    public void deleteAllByMemberId(Long memberId) {
        tokens.entrySet().removeIf(entry -> entry.getValue().memberId().equals(memberId));
    }

    private record LoginToken(Long memberId, Instant expiresAt) {}
}
