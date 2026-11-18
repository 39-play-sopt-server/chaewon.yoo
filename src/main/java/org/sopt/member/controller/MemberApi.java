package org.sopt.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import java.util.List;
import org.sopt.member.dto.response.MemberResponse;
import org.sopt.global.response.ApiResponse;

@Tag(name = "회원", description = "내 정보 조회 및 탈퇴")
public interface MemberApi {
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "전체 회원 목록 조회", description = "ROLE_ADMIN 권한의 관리자만 조회할 수 있습니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 목록 조회 완료")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 필요")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한 필요")
    ResponseEntity<ApiResponse<List<MemberResponse>>> readMembers();

    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 필요 또는 토큰 만료")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "탈퇴 완료, 모든 로그인 토큰 무효화")
    @Operation(summary = "회원 삭제", description = "게시글은 보존하고 작성자 연결을 해제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 회원")
    ResponseEntity<ApiResponse<Void>> deleteMember(@Parameter(hidden = true) Long memberId);
}
