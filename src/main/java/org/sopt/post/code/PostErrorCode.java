package org.sopt.post.code;

import org.springframework.http.HttpStatus;
import org.sopt.global.code.ErrorCode;
import org.sopt.post.constant.PostLimits;

public enum PostErrorCode implements ErrorCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    POST_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "제목을 입력해주세요."),
    POST_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "제목은 " + PostLimits.MAX_TITLE_LENGTH + "자 이하여야 합니다."),
    POST_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "내용을 입력해주세요."),
    POST_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "내용은 " + PostLimits.MAX_CONTENT_LENGTH + "자 이하여야 합니다."),
    POST_CATEGORY_REQUIRED(HttpStatus.BAD_REQUEST, "카테고리를 선택해주세요."),
    POST_INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "존재하지 않는 카테고리입니다."),
    INVALID_PAGE(HttpStatus.BAD_REQUEST, "page는 0 이상이어야 합니다."),
    INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, "size는 1 이상이어야 합니다.");

    private final HttpStatus status;
    private final String message;

    PostErrorCode(HttpStatus status, String message) {
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
