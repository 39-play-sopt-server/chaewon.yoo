package org.sopt.server.dto.response;

import org.sopt.server.model.Post;
import java.time.LocalDateTime;

public record PostResponse(
        long id,
        String title,
        String content,
        String category,
        String tag,
        LocalDateTime createdAt
) {
    public PostResponse(Post post) {
        this(post.getId(), post.getTitle(), post.getContent(),
                post.getCategory().name(), post.getTag(), post.getCreatedAt());
    }
}
