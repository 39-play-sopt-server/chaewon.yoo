package org.sopt.server.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sopt.server.exception.ExceptionHandler;
import org.sopt.server.model.Category;
import org.sopt.server.model.Post;
import org.sopt.server.dto.response.PostResponse;
import org.sopt.server.dto.request.CreatePostRequest;
import org.sopt.server.dto.request.UpdatePostRequest;
import org.sopt.common.response.Response;
import org.sopt.common.response.SuccessMessage;
import org.sopt.server.service.PostService;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService service;
    private final ExceptionHandler exceptionHandler;

    public PostController(PostService service, ExceptionHandler exceptionHandler) {
        this.service = service;
        this.exceptionHandler = exceptionHandler;
    }

    @PostMapping
    public Response<PostResponse> createPost(@RequestBody CreatePostRequest request) {
        return exceptionHandler.execute(() -> {
            Post post = service.createPost(
                    request.title(), request.content(),
                    Category.fromNumber(request.category()), request.tag());

            return Response.success(SuccessMessage.POST_CREATED, new PostResponse(post));
        });
    }

    @GetMapping
    public Response<List<PostResponse>> readPosts() {
        return exceptionHandler.execute(() -> {
            List<PostResponse> data = new ArrayList<>();
            List<Post> posts = service.readPosts();
            for (int i = 0; i < posts.size(); i++) {
                data.add(new PostResponse(posts.get(i)));
            }
            return Response.success(SuccessMessage.POSTS_READ, data);
        });
    }

    @GetMapping("/{id}")
    public Response<PostResponse> readPost(@PathVariable("id") long id) {
        return exceptionHandler.execute(() -> {
            Post post = service.readPost(id);

            return Response.success(SuccessMessage.POST_READ, new PostResponse(post));
        });
    }

    public Response<Void> existsPost(long id) {
        return exceptionHandler.execute(() -> {
            service.validatePostExists(id);
            return Response.success(SuccessMessage.POST_EXISTS);
        });
    }

    @PutMapping("/{id}")
    public Response<PostResponse> updatePost(@PathVariable("id") long id, @RequestBody UpdatePostRequest request) {
        return exceptionHandler.execute(() -> {
            Post post = service.updatePost(
                    id, request.title(), request.content(),
                    Category.fromNumber(request.category()), request.tag());

            return Response.success(SuccessMessage.POST_UPDATED, new PostResponse(post));
        });
    }

    @DeleteMapping("/{id}")
    public Response<Void> deletePost(@PathVariable("id") long id) {
        return exceptionHandler.execute(() -> {
            service.deletePost(id);

            return Response.success(SuccessMessage.POST_DELETED);
        });
    }

}
