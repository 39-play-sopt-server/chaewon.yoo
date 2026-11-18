package org.sopt.member.code;

import org.springframework.http.HttpStatus;
import org.sopt.global.code.SuccessCode;

public enum MemberSuccessCode implements SuccessCode {
    MEMBERS_READ(HttpStatus.OK, "회원 목록을 조회했습니다."),
    MEMBER_DELETED(HttpStatus.OK, "회원이 삭제되었습니다.");

    private final HttpStatus status;
    private final String message;

    MemberSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
