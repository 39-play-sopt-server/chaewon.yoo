package org.sopt.server.model;

import java.time.LocalDateTime;
import org.sopt.server.validate.PostValidator;

public class Post {
    private final long id;
    private String title;
    private String content;
    private Category category;
    private String tag;
    private final LocalDateTime createdAt;

    public Post(long id, String title, String content, Category category, String tag) {
        PostValidator.validate(title, content, category, tag);
        this.title = title;
        this.content = content;
        this.category = category;
        if (tag == null) {
            this.tag = "";
        } else {
            this.tag = tag;
        }
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

    public Category getCategory() {
        return this.category;
    }

    public String getTag() {
        return this.tag;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void update(String title, String content, Category category, String tag) {
        PostValidator.validate(title, content, category, tag);
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
