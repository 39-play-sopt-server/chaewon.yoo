package org.sopt.server.dto.response;

import org.sopt.server.model.Post;
import java.time.LocalDateTime;

public class PostResponse {
    private final long id;
    private final String title;
    private final String content;
    private final String category;
    private final String tag;
    private final LocalDateTime createdAt;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.category = post.getCategory().name();
        this.tag = post.getTag();
        this.createdAt = post.getCreatedAt();
    }

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public String getCategory() {
        return this.category;
    }

    public String getTag() {
        return this.tag;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
}
