package org.sopt.global.security;

import java.time.Instant;

public record JwtTokenClaims(Long memberId, String tokenId, Instant expiresAt) {}
