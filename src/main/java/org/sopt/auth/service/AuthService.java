package org.sopt.auth.service;

import io.jsonwebtoken.JwtException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.UUID;
import org.sopt.global.security.JwtTokenClaims;
import org.sopt.auth.dto.response.TokenResponse;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.code.GlobalErrorCode;
import org.sopt.global.security.JwtProvider;
import org.sopt.member.domain.Member;
import org.sopt.member.repository.MemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public Long signup(String name, String email, String password) {
        validateCredentials(email, password);
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(GlobalErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Member member = new Member(name, email, passwordEncoder.encode(password));
        try {
            return memberRepository.saveAndFlush(member).getId();
        } catch (DataIntegrityViolationException exception) {
            for (Throwable cause = exception.getCause(); cause != null; cause = cause.getCause()) {
                if (cause instanceof SQLException sqlException && sqlException.getErrorCode() == 1062) {
                    throw new BusinessException(GlobalErrorCode.EMAIL_ALREADY_EXISTS);
                }
            }
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public TokenResponse login(String email, String password) {
        validateCredentials(email, password);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.INVALID_CREDENTIALS));

        if (member.getPassword() == null || !passwordEncoder.matches(password, member.getPassword())) {
            throw new BusinessException(GlobalErrorCode.INVALID_CREDENTIALS);
        }

        return issueTokens(member.getId());
    }

    @Transactional(readOnly = true)
    public TokenResponse loginSocial(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new BusinessException(GlobalErrorCode.SOCIAL_LOGIN_FAILED);
        }
        return issueTokens(memberId);
    }

    private TokenResponse issueTokens(Long memberId) {
        String tokenId = UUID.randomUUID().toString();
        String refreshToken = jwtProvider.createRefreshToken(memberId, tokenId);
        return new TokenResponse(jwtProvider.createAccessToken(memberId, tokenId), refreshToken);
    }

    @Transactional(readOnly = true)
    public String refresh(String refreshToken) {
        JwtTokenClaims claims;
        try {
            claims = jwtProvider.getRefreshTokenClaims(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(GlobalErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (!memberRepository.existsById(claims.memberId())) {
            throw new BusinessException(GlobalErrorCode.INVALID_REFRESH_TOKEN);
        }
        return jwtProvider.createAccessToken(claims.memberId(), claims.tokenId());
    }

    public void logout(String accessToken) {
        try {
            jwtProvider.invalidateSession(accessToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(GlobalErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    private void validateCredentials(String email, String password) {
        if (email == null || email.length() > 255
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                || password == null || password.isBlank()
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BusinessException(GlobalErrorCode.INVALID_SIGNUP);
        }
    }
}
