package org.sopt.post.exception;

import org.sopt.global.exception.BusinessException;
import org.sopt.post.code.PostErrorCode;

public class PostException extends BusinessException {
    public PostException(PostErrorCode errorCode) {
        super(errorCode);
    }
}
