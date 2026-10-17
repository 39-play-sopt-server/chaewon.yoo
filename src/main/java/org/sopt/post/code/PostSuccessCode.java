package org.sopt.post.code;

import org.springframework.http.HttpStatus;
import org.sopt.global.code.SuccessCode;

public enum PostSuccessCode implements SuccessCode {
    POST_CREATED(HttpStatus.CREATED, "게시글이 작성되었습니다."),
    POSTS_READ(HttpStatus.OK, "게시글 목록을 조회했습니다."),
    POST_READ(HttpStatus.OK, "게시글을 조회했습니다."),
    POST_UPDATED(HttpStatus.OK, "게시글이 수정되었습니다."),
    POST_DELETED(HttpStatus.OK, "게시글이 삭제되었습니다.");

    private final HttpStatus status;
    private final String message;

    PostSuccessCode(HttpStatus status, String message) {
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
