package org.sopt.member.code;

import org.springframework.http.HttpStatus;
import org.sopt.global.code.ErrorCode;

public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    MEMBER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "회원 ID를 입력해주세요."),
    MEMBER_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "회원 이름을 입력해주세요."),
    MEMBER_NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "회원 이름은 100자 이하여야 합니다.");

    private final HttpStatus status;
    private final String message;

    MemberErrorCode(HttpStatus status, String message) {
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
