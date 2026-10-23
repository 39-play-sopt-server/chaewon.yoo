package org.sopt.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.sopt.global.response.ApiResponse;
import org.sopt.member.dto.request.CreateMemberRequest;
import org.sopt.member.dto.response.MemberResponse;

@Tag(name = "회원", description = "회원 생성 및 삭제")
public interface MemberApi {
    @Operation(summary = "회원 생성")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원 생성 완료")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 이름이 올바르지 않음")
    ResponseEntity<ApiResponse<MemberResponse>> createMember(CreateMemberRequest request);

    @Operation(summary = "회원 삭제", description = "게시글은 보존하고 작성자 연결을 해제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 회원")
    ResponseEntity<ApiResponse<Void>> deleteMember(long id);
}
