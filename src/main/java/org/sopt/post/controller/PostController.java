package org.sopt.post.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.sopt.global.response.ApiResponse;
import org.sopt.post.code.PostSuccessCode;
import org.sopt.post.dto.request.CreatePostRequest;
import org.sopt.post.dto.request.LikePostRequest;
import org.sopt.post.dto.request.UpdatePostRequest;
import org.sopt.post.dto.response.PostResponse;
import org.sopt.post.domain.PostCategory;
import org.sopt.post.service.PostService;
import org.sopt.post.dto.response.PostPageResponse;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController implements PostApi {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@RequestBody CreatePostRequest request) {
        PostResponse post = service.createPost(
                request.memberId(), request.title(), request.content(),
                PostCategory.fromNumber(request.category()), request.tag());

        return ResponseEntity.status(PostSuccessCode.POST_CREATED.getStatus())
                .body(ApiResponse.success(PostSuccessCode.POST_CREATED, post));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PostPageResponse>> readPosts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "memberId", required = false) Long memberId,
            @RequestParam(name = "category", required = false) Integer category) {
        PostCategory filter = category == null ? null : PostCategory.fromNumber(category);
        PostPageResponse posts = service.readPosts(page, size, memberId, filter);
        return ResponseEntity.status(PostSuccessCode.POSTS_READ.getStatus())
                .body(ApiResponse.success(PostSuccessCode.POSTS_READ, posts));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PostPageResponse>> searchPosts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "category", required = false) Integer category,
            @RequestParam(name = "title", required = false) String title) {
        PostCategory filter = category == null ? null : PostCategory.fromNumber(category);
        PostPageResponse posts = service.searchPosts(page, size, filter, title);
        return ResponseEntity.status(PostSuccessCode.POSTS_READ.getStatus())
                .body(ApiResponse.success(PostSuccessCode.POSTS_READ, posts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> readPost(@PathVariable("id") long id) {
        PostResponse post = service.readPost(id);
        return ResponseEntity.status(PostSuccessCode.POST_READ.getStatus())
                .body(ApiResponse.success(PostSuccessCode.POST_READ, post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable("id") long id, @RequestBody UpdatePostRequest request) {
        PostResponse post = service.updatePost(
                id, request.title(), request.content(),
                PostCategory.fromNumber(request.category()), request.tag());

        return ResponseEntity.status(PostSuccessCode.POST_UPDATED.getStatus())
                .body(ApiResponse.success(PostSuccessCode.POST_UPDATED, post));
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<ApiResponse<Void>> likePost(
            @PathVariable("id") long id, @RequestBody LikePostRequest request) {
        service.likePost(id, request.memberId());
        return ResponseEntity.status(PostSuccessCode.POST_LIKED.getStatus())
                .body(ApiResponse.success(PostSuccessCode.POST_LIKED));
    }

    @DeleteMapping("/{id}/likes")
    public ResponseEntity<ApiResponse<Void>> cancelLike(
            @PathVariable("id") long id, @RequestParam(name = "memberId") Long memberId) {
        service.cancelLike(id, memberId);
        return ResponseEntity.status(PostSuccessCode.POST_LIKE_CANCELED.getStatus())
                .body(ApiResponse.success(PostSuccessCode.POST_LIKE_CANCELED));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable("id") long id) {
        service.deletePost(id);
        return ResponseEntity.status(PostSuccessCode.POST_DELETED.getStatus())
                .body(ApiResponse.success(PostSuccessCode.POST_DELETED));
    }
}
