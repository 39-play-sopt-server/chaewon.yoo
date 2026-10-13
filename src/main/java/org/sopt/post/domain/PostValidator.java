package org.sopt.post.domain;

import org.sopt.post.exception.PostException;
import org.sopt.post.code.PostErrorCode;

public class PostValidator {
    public static void validate(String title, String content, PostCategory category) {
        if (title == null || title.isBlank()) {
            throw new PostException(PostErrorCode.POST_TITLE_REQUIRED);
        }
        if (title.length() > PostLimits.MAX_TITLE_LENGTH) {
            throw new PostException(PostErrorCode.POST_TITLE_TOO_LONG);
        }
        if (content == null || content.isBlank()) {
            throw new PostException(PostErrorCode.POST_CONTENT_REQUIRED);
        }
        if (content.length() > PostLimits.MAX_CONTENT_LENGTH) {
            throw new PostException(PostErrorCode.POST_CONTENT_TOO_LONG);
        }
        if (category == null) {
            throw new PostException(PostErrorCode.POST_CATEGORY_REQUIRED);
        }
    }
}
