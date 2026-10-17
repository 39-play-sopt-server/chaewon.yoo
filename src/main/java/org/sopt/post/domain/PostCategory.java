package org.sopt.post.domain;

import org.sopt.post.exception.PostException;
import org.sopt.post.code.PostErrorCode;
import java.util.Locale;

public enum PostCategory {
    FREE, QUESTION, INFO;

    public static PostCategory fromName(String name) {
        try {
            return PostCategory.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new PostException(PostErrorCode.POST_INVALID_CATEGORY);
        }
    }

    public static PostCategory fromNumber(Integer number) {
        if (number == null) {
            throw new PostException(PostErrorCode.POST_CATEGORY_REQUIRED);
        }
        return switch (number) {
            case 1 -> FREE;
            case 2 -> QUESTION;
            case 3 -> INFO;
            default -> throw new PostException(PostErrorCode.POST_INVALID_CATEGORY);
        };
    }
}
