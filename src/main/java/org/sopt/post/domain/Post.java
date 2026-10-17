package org.sopt.post.domain;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = PostLimits.MAX_TITLE_LENGTH)
    private String title;

    @Column(length = PostLimits.MAX_CONTENT_LENGTH)
    private String content;

    @Enumerated(EnumType.STRING)
    private PostCategory category;

    private String tag;
    private LocalDateTime createdAt;
    private long viewCount;

    protected Post() {}

    public Post(String title, String content, PostCategory category, String tag) {
        update(title, content, category, tag);
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
