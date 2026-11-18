package org.sopt.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
public class TokenCookieProvider {
    private final long refreshTokenExpiration;

    public TokenCookieProvider(@Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public ResponseCookie createRefreshCookie(String token) {
        return refreshCookie(token, Duration.ofMillis(refreshTokenExpiration));
    }

    public ResponseCookie clearRefreshCookie() {
        return refreshCookie("", Duration.ZERO);
    }

    private ResponseCookie refreshCookie(String token, Duration maxAge) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth/refresh")
                .maxAge(maxAge)
                .build();
    }
}
