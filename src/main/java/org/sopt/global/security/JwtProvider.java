package org.sopt.global.security;

import io.jsonwebtoken.JwtException;
import org.sopt.auth.repository.TokenStore;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private final JwtTokenIssuer jwtTokenIssuer;
    private final JwtTokenParser jwtTokenParser;
    private final TokenStore tokenStore;

    public JwtProvider(JwtTokenIssuer jwtTokenIssuer, JwtTokenParser jwtTokenParser, TokenStore tokenStore) {
        this.jwtTokenIssuer = jwtTokenIssuer;
        this.jwtTokenParser = jwtTokenParser;
        this.tokenStore = tokenStore;
    }

    public String createAccessToken(Long memberId, String tokenId) {
        return jwtTokenIssuer.createToken(memberId, TokenType.ACCESS, tokenId);
    }

    public String createRefreshToken(Long memberId, String tokenId) {
        String token = jwtTokenIssuer.createToken(memberId, TokenType.REFRESH, tokenId);
        JwtTokenClaims claims = jwtTokenParser.parse(token, TokenType.REFRESH);
        tokenStore.save(tokenId, memberId, claims.expiresAt());
        return token;
    }

    public Long getMemberId(String token) {
        return validateToken(token, TokenType.ACCESS).memberId();
    }

    public JwtTokenClaims getRefreshTokenClaims(String token) {
        return validateToken(token, TokenType.REFRESH);
    }

    public void invalidateSession(String accessToken) {
        JwtTokenClaims claims = validateToken(accessToken, TokenType.ACCESS);
        tokenStore.delete(claims.tokenId());
    }

    private JwtTokenClaims validateToken(String token, TokenType tokenType) {
        JwtTokenClaims claims = jwtTokenParser.parse(token, tokenType);
        if (!tokenStore.isActive(claims.tokenId(), claims.memberId())) {
            throw new JwtException("만료되거나 로그아웃한 토큰입니다.");
        }
        return claims;
    }
}
