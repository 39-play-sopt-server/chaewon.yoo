package org.sopt.post.dto.request;

public record CreatePostRequest(Long memberId, String title, String content, Integer category, String tag) {
}
