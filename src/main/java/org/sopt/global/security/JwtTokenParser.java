package org.sopt.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtTokenParser {

    private final SecretKey secretKey;

    public JwtTokenParser(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public JwtTokenClaims parse(String token, TokenType tokenType) {
        Claims claims = Jwts.parser()
                .require("tokenType", tokenType.name())
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (claims.getId() == null || claims.getId().isBlank() || claims.getExpiration() == null) {
            throw new JwtException("토큰 식별자 또는 만료 시간이 없습니다.");
        }
        return new JwtTokenClaims(Long.valueOf(claims.getSubject()),
                claims.getId(), claims.getExpiration().toInstant());
    }
}
