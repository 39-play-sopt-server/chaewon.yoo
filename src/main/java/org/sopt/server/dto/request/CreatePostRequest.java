package org.sopt.server.dto.request;

public record CreatePostRequest(String title, String content, int category, String tag) {
}
