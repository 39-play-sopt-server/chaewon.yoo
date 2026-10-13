package org.sopt.post.dto.request;

public record CreatePostRequest(String title, String content, Integer category, String tag) {
}
