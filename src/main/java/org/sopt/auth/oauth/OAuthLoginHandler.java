package org.sopt.auth.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.sopt.auth.dto.response.LoginResponse;
import org.sopt.auth.dto.response.SocialUserInfo;
import org.sopt.auth.dto.response.TokenResponse;
import org.sopt.auth.service.AuthService;
import org.sopt.auth.service.SocialMemberService;
import org.sopt.global.code.ErrorCode;
import org.sopt.global.code.GlobalErrorCode;
import org.sopt.global.exception.BusinessException;
import org.sopt.global.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.sopt.auth.service.TokenCookieProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;

@Component
public class OAuthLoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuthLoginHandler.class);

    private final List<SocialUserInfoExtractor> extractors;
    private final SocialMemberService socialMemberService;
    private final AuthService authService;
    private final JsonMapper jsonMapper;
    private final TokenCookieProvider tokenCookieProvider;

    public OAuthLoginHandler(List<SocialUserInfoExtractor> extractors, SocialMemberService socialMemberService,
                             AuthService authService, JsonMapper jsonMapper,
                             TokenCookieProvider tokenCookieProvider) {
        this.extractors = extractors;
        this.socialMemberService = socialMemberService;
        this.authService = authService;
        this.jsonMapper = jsonMapper;
        this.tokenCookieProvider = tokenCookieProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        prepareResponse(response);
        try {
            OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;
            SocialUserInfoExtractor extractor = extractors.stream()
                    .filter(candidate -> candidate.supports(oauth.getAuthorizedClientRegistrationId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(GlobalErrorCode.SOCIAL_LOGIN_FAILED));
            SocialUserInfo userInfo = extractor.extract(oauth.getPrincipal().getAttributes());
            Long memberId = socialMemberService.findOrCreate(userInfo);
            TokenResponse tokens = authService.loginSocial(memberId);
            String cookie = tokenCookieProvider.createRefreshCookie(tokens.refreshToken()).toString();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.accessToken());
            response.addHeader(HttpHeaders.SET_COOKIE, cookie);
            response.getWriter().write(jsonMapper.writeValueAsString(new LoginResponse(tokens.accessToken())));
        } catch (BusinessException e) {
            writeFailure(response, e.getErrorCode());
        } catch (DataIntegrityViolationException e) {
            writeFailure(response, constraintError(e));
        } finally {
            clearSession(request);
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        try {
            prepareResponse(response);
            writeFailure(response, GlobalErrorCode.SOCIAL_LOGIN_FAILED);
        } finally {
            clearSession(request);
        }
    }

    private ErrorCode constraintError(DataIntegrityViolationException exception) {
        for (Throwable cause = exception.getCause(); cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation) {
                String name = violation.getConstraintName();
                if ("uk_member_email".equals(name) || (name != null && name.endsWith(".uk_member_email"))) {
                    return GlobalErrorCode.SOCIAL_EMAIL_CONFLICT;
                }
                if ("uk_social_provider_user".equals(name)
                        || (name != null && name.endsWith(".uk_social_provider_user"))) {
                    return GlobalErrorCode.SOCIAL_ACCOUNT_CONFLICT;
                }
            }
        }
        log.error("소셜 로그인 중 DB 제약 오류가 발생했습니다.", exception);
        return GlobalErrorCode.SERVER_ERROR;
    }

    private void prepareResponse(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader("Referrer-Policy", "no-referrer");
    }

    private void writeFailure(HttpServletResponse response, ErrorCode code) throws IOException {
        response.setStatus(code.getStatus().value());
        response.getWriter().write(jsonMapper.writeValueAsString(ApiResponse.fail(code)));
    }

    private void clearSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }
}
