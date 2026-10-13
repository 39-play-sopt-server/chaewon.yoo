package org.sopt.post.domain;

import java.time.LocalDateTime;

public class Post {
    private final long id;
    private String title;
    private String content;
    private PostCategory category;
    private String tag;
    private final LocalDateTime createdAt;
    private long viewCount;

    public Post(long id, String title, String content, PostCategory category, String tag) {
        update(title, content, category, tag);
        this.id = id;
        this.createdAt = LocalDateTime.now();
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

    public PostCategory getCategory() {
        return this.category;
    }

    public String getTag() {
        return this.tag;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public long getViewCount() {
        return this.viewCount;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void update(String title, String content, PostCategory category, String tag) {
        PostValidator.validate(title, content, category);
        this.title = title;
        this.content = content;
        this.category = category;
        if (tag == null) {
            this.tag = "";
        } else {
            this.tag = tag;
        }
    }
}
