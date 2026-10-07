package org.sopt.server.validate;

import org.sopt.server.model.Category;

public class PostValidator {
    public static void validate(String title, String content, Category category, String tag) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목을 입력해주세요.");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("제목은 100자 이하여야 합니다.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용을 입력해주세요.");
        }
        if (content.length() > 5000) {
            throw new IllegalArgumentException("내용은 5000자 이하여야 합니다.");
        }
        if (category == null) {
            throw new IllegalArgumentException("카테고리를 선택해주세요.");
        }
    }
}
