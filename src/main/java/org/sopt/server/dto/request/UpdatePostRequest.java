package org.sopt.server.dto.request;

public record UpdatePostRequest(String title, String content, int category, String tag) {
}
