package org.sopt.global.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenIssuer {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenIssuer(SecretKey secretKey,
                          @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
                          @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.secretKey = secretKey;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createToken(Long memberId, TokenType tokenType, String tokenId) {
        Date now = new Date();
        long expiration = switch (tokenType) {
            case ACCESS -> accessTokenExpiration;
            case REFRESH -> refreshTokenExpiration;
        };

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .id(tokenId)
                .claim("tokenType", tokenType.name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(secretKey)
                .compact();
    }
}
