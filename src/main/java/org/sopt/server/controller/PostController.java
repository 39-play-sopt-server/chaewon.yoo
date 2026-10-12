package org.sopt.server.controller;

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

public class PostController {
    private final PostService service;
    private final ExceptionHandler exceptionHandler;

    public PostController(PostService service, ExceptionHandler exceptionHandler) {
        this.service = service;
        this.exceptionHandler = exceptionHandler;
    }

    public Response<PostResponse> createPost(CreatePostRequest request) {
        return exceptionHandler.execute(() -> {
            Post post = service.createPost(
                    request.title(), request.content(),
                    Category.fromNumber(request.category()), request.tag());

            return Response.success(SuccessMessage.POST_CREATED, new PostResponse(post));
        });
    }

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

    public Response<PostResponse> readPost(long id) {
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

    public Response<PostResponse> updatePost(long id, UpdatePostRequest request) {
        return exceptionHandler.execute(() -> {
            Post post = service.updatePost(
                    id, request.title(), request.content(),
                    Category.fromNumber(request.category()), request.tag());

            return Response.success(SuccessMessage.POST_UPDATED, new PostResponse(post));
        });
    }

    public Response<Void> deletePost(long id) {
        return exceptionHandler.execute(() -> {
            service.deletePost(id);

            return Response.success(SuccessMessage.POST_DELETED);
        });
    }

}
