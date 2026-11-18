package org.sopt.global.code;

import org.springframework.http.HttpStatus;

public enum GlobalErrorCode implements ErrorCode {
    INVALID_CSRF_TOKEN(HttpStatus.FORBIDDEN, "CSRF 토큰이 없거나 올바르지 않습니다. 토큰을 다시 발급받아주세요."),
    SOCIAL_ACCOUNT_CONFLICT(HttpStatus.CONFLICT, "소셜 계정이 이미 등록되었습니다. 다시 로그인해주세요."),
    SOCIAL_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "소셜 로그인에 실패했습니다. 다시 시도해주세요."),
    SOCIAL_EMAIL_CONFLICT(HttpStatus.CONFLICT, "이미 가입된 이메일입니다. 기존 로그인 방법을 사용해주세요."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    INVALID_SIGNUP(HttpStatus.BAD_REQUEST, "이메일 형식과 비밀번호를 확인해주세요. 비밀번호는 공백일 수 없고 UTF-8 기준 72바이트 이하여야 합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 Access Token입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 Refresh Token입니다. 다시 로그인해주세요."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 형식이나 입력값을 확인해주세요."),
    PATH_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 경로를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 요청 메서드입니다."),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "지원하지 않는 응답 형식입니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 Content-Type입니다."),
    DATABASE_CONFLICT(HttpStatus.CONFLICT, "동시 요청으로 DB 잠금 충돌이 발생했습니다. 잠시 후 다시 요청해주세요."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    GlobalErrorCode(HttpStatus status, String message) {
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
