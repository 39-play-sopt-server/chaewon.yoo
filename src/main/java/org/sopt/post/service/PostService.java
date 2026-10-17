package org.sopt.post.service;

import org.springframework.stereotype.Service;
import org.sopt.post.dto.response.PostResponse;
import org.sopt.post.domain.PostCategory;
import org.sopt.post.domain.Post;
import org.sopt.post.repository.PostRepository;
import org.sopt.post.exception.PostException;
import org.sopt.post.code.PostErrorCode;
import java.util.List;

@Service
public class PostService {
    private final PostRepository repository;
    private final Object postLock = new Object();

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public PostResponse createPost(String title, String content, PostCategory category, String tag) {
        synchronized (postLock) {
            long id = repository.createId();
            Post post = new Post(id, title, content, category, tag);
            repository.save(post);
            return PostResponse.from(post);
        }
    }

    public List<PostResponse> readPosts(int page, int size, PostCategory category) {
        if (page < 0) {
            throw new PostException(PostErrorCode.INVALID_PAGE);
        }
        if (size < 1) {
            throw new PostException(PostErrorCode.INVALID_PAGE_SIZE);
        }
        synchronized (postLock) {
            return repository.findAll().stream()
                    .filter(post -> category == null || post.getCategory() == category)
                    .skip((long) page * size)
                    .limit(size)
                    .map(PostResponse::from)
                    .toList();
        }
    }

    public PostResponse readPost(long id) {
        synchronized (postLock) {
            Post post = findPostOrThrow(id);
            post.increaseViewCount();
            return PostResponse.from(post);
        }
    }

    public PostResponse updatePost(long id, String title, String content, PostCategory category, String tag) {
        synchronized (postLock) {
            Post post = findPostOrThrow(id);
            post.update(title, content, category, tag);
            return PostResponse.from(post);
        }
    }

    private Post findPostOrThrow(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
    }

    public void deletePost(long id) {
        synchronized (postLock) {
            findPostOrThrow(id);
            repository.deleteById(id);
        }
    }
}
