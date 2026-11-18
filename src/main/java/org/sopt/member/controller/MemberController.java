package org.sopt.member.controller;

import org.springframework.http.ResponseEntity;
import java.util.List;
import org.sopt.member.dto.response.MemberResponse;
import org.springframework.http.HttpHeaders;
import org.sopt.auth.service.TokenCookieProvider;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sopt.global.response.ApiResponse;
import org.sopt.member.code.MemberSuccessCode;
import org.sopt.member.service.MemberService;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController implements MemberApi {
    private final MemberService service;
    private final TokenCookieProvider tokenCookieProvider;

    public MemberController(MemberService service, TokenCookieProvider tokenCookieProvider) {
        this.service = service;
        this.tokenCookieProvider = tokenCookieProvider;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> readMembers() {
        return ResponseEntity.status(MemberSuccessCode.MEMBERS_READ.getStatus())
                .body(ApiResponse.success(MemberSuccessCode.MEMBERS_READ, service.readMembers()));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "내 회원 ID 조회")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내 회원 ID 반환")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 필요 또는 토큰 만료")
    @GetMapping("/me")
    public ResponseEntity<Long> me(@io.swagger.v3.oas.annotations.Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(memberId);
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@AuthenticationPrincipal Long memberId) {
        service.deleteMember(memberId);
        String cookie = tokenCookieProvider.clearRefreshCookie().toString();
        return ResponseEntity.status(MemberSuccessCode.MEMBER_DELETED.getStatus())
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body(ApiResponse.success(MemberSuccessCode.MEMBER_DELETED));
    }
}
