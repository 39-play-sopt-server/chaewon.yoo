package org.sopt.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.auth.dto.request.LoginRequest;
import org.sopt.auth.dto.request.SignupRequest;
import org.sopt.auth.dto.response.LoginResponse;
import org.sopt.auth.dto.response.TokenResponse;
import org.springframework.http.HttpHeaders;
import org.sopt.auth.service.TokenCookieProvider;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.sopt.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "인증", description = "회원가입, 로그인, 토큰 재발급 및 로그아웃")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenCookieProvider tokenCookieProvider;

    public AuthController(AuthService authService,
                          TokenCookieProvider tokenCookieProvider) {
        this.authService = authService;
        this.tokenCookieProvider = tokenCookieProvider;
    }

    @Operation(summary = "CSRF 토큰 발급", description = "응답의 token을 로그인·재발급 요청의 X-XSRF-TOKEN 헤더로 보내고 발급된 쿠키도 유지해주세요.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "CSRF 토큰 발급 완료")
    @GetMapping("/csrf")
    public CsrfToken csrf(@Parameter(hidden = true) CsrfToken csrfToken) {
        return csrfToken;
    }

    @Operation(summary = "회원가입")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 완료, 회원 ID 반환")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이름, 이메일 또는 비밀번호가 올바르지 않음")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 가입된 이메일")
    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.status(201).body(authService.signup(request.name(), request.email(), request.password()));
    }

    @Operation(summary = "로그인", description = "CSRF 발급 후 요청해주세요. Access Token은 본문과 Authorization 헤더, Refresh Token은 HttpOnly 쿠키로 반환합니다.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER, required = true, description = "/auth/csrf 응답의 token")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 완료")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이메일 또는 비밀번호 형식이 올바르지 않음")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "CSRF 토큰이 없거나 올바르지 않음")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        TokenResponse tokens = authService.login(request.email(), request.password());
        String cookie = tokenCookieProvider.createRefreshCookie(tokens.refreshToken()).toString();

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.accessToken())
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body(new LoginResponse(tokens.accessToken()));
    }

    @Operation(summary = "Access Token 재발급", description = "로그인 때 받은 refreshToken 쿠키와 CSRF 헤더가 필요합니다. 브라우저는 쿠키를 자동 전송합니다.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER, required = true, description = "/auth/csrf 응답의 token")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Access Token 재발급 완료")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh Token이 없거나 만료·무효화됨")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "CSRF 토큰이 없거나 올바르지 않음")
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @Parameter(hidden = true) @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        String accessToken = authService.refresh(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(new LoginResponse(accessToken));
    }

    @Operation(summary = "로그아웃", description = "현재 로그인 세션의 Access Token과 Refresh Token을 무효화하고 쿠키를 만료시킵니다.")
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "로그아웃 완료")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 필요 또는 토큰 만료")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        authService.logout(authorization.substring("Bearer ".length()));
        String cookie = tokenCookieProvider.clearRefreshCookie().toString();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie)
                .build();
    }
}
