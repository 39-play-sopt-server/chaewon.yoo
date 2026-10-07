package org.sopt.client.view;

import org.sopt.server.dto.response.PostResponse;
import org.sopt.common.response.Response;
import java.util.List;

public class OutputView {
    public void printMenu() {
        System.out.println("\n=== 게시판 ===");
        System.out.println("1. 게시글 작성");
        System.out.println("2. 게시글 목록 조회");
        System.out.println("3. 게시글 단건 조회");
        System.out.println("4. 게시글 수정");
        System.out.println("5. 게시글 삭제");
        System.out.println("6. 종료");
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printResponse(Response<?> response) {
        printMessage(response.getMessage());
    }

    public void printPosts(List<PostResponse> posts) {
        if (posts.isEmpty()) {
            printMessage("게시글이 없습니다.");
            return;
        }
        for (int i = 0; i < posts.size(); i++) {
            PostResponse post = posts.get(i);
            printMessage(post.getId() + ". " + post.getTitle());
        }
    }

    public void printPost(PostResponse post) {
        System.out.println("\n=== 게시글 ===");
        System.out.println("ID: " + post.getId());
        System.out.println("제목: " + post.getTitle());
        System.out.println("내용: " + post.getContent());
        System.out.println("태그: " + post.getTag());
        System.out.println("카테고리: " + post.getCategory());
        System.out.println("작성일: " + post.getCreatedAt());
    }
}
