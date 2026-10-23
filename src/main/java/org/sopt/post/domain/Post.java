package org.sopt.post.domain;

import org.sopt.post.constant.PostLimits;
import org.sopt.global.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import org.sopt.member.domain.Member;

@Entity
public class Post extends BaseEntity {
    @Column(nullable = false, length = PostLimits.MAX_TITLE_LENGTH)
    private String title;

    @Column(nullable = false, length = PostLimits.MAX_CONTENT_LENGTH)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String tag;
    private long viewCount;

    protected Post() {}

    public Post(Member member, String title, String content, PostCategory category, String tag) {
        this.member = member;
        update(title, content, category, tag);
    }

    public Member getMember() {
        return member;
    }

    public void removeAuthor() {
        this.member = null;
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
