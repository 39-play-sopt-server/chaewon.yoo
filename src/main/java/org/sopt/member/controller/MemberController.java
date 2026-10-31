package org.sopt.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sopt.global.response.ApiResponse;
import org.sopt.member.code.MemberSuccessCode;
import org.sopt.member.dto.request.CreateMemberRequest;
import org.sopt.member.dto.response.MemberResponse;
import org.sopt.member.service.MemberService;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController implements MemberApi {
    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> createMember(@RequestBody CreateMemberRequest request) {
        MemberResponse member = service.createMember(request.name());
        return ResponseEntity.status(MemberSuccessCode.MEMBER_CREATED.getStatus())
                .body(ApiResponse.success(MemberSuccessCode.MEMBER_CREATED, member));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable("id") long id) {
        service.deleteMember(id);
        return ResponseEntity.status(MemberSuccessCode.MEMBER_DELETED.getStatus())
                .body(ApiResponse.success(MemberSuccessCode.MEMBER_DELETED));
    }
}
