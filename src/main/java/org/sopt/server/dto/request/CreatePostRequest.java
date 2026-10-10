package org.sopt.server.dto.request;

public class CreatePostRequest {
    private final String title;
    private final String content;
    private final int category;
    private final String tag;

    public CreatePostRequest(String title, String content, int category, String tag) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.tag = tag;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public int getCategory() {
        return this.category;
    }

    public String getTag() {
        return this.tag;
    }
}
