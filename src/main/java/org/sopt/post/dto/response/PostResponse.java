package org.sopt.post.dto.response;

import org.sopt.post.domain.Post;
import java.time.LocalDateTime;

public record PostResponse(
        long id,
        String title,
        String content,
        String category,
        String tag,
        LocalDateTime createdAt,
        long viewCount
) {
    public static PostResponse from(Post post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent(),
                post.getCategory().name(), post.getTag(), post.getCreatedAt(), post.getViewCount());
    }
}
