package org.sopt.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import org.sopt.post.dto.response.PostResponse;
import org.sopt.post.domain.PostCategory;
import org.sopt.post.domain.Post;
import org.sopt.post.repository.PostRepository;
import org.sopt.post.exception.PostException;
import org.sopt.post.code.PostErrorCode;
import java.util.List;

@Service
@Transactional
public class PostService {
    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public PostResponse createPost(String title, String content, PostCategory category, String tag) {
        Post post = new Post(title, content, category, tag);
        repository.save(post);
        return PostResponse.from(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> readPosts(int page, int size, PostCategory category) {
        if (page < 0) {
            throw new PostException(PostErrorCode.INVALID_PAGE);
        }
        if (size < 1) {
            throw new PostException(PostErrorCode.INVALID_PAGE_SIZE);
        }
        return repository.findAll(Sort.by("id")).stream()
                .filter(post -> category == null || post.getCategory() == category)
                .skip((long) page * size)
                .limit(size)
                .map(PostResponse::from)
                .toList();
    }

    public PostResponse readPost(long id) {
        Post post = findPostOrThrow(id);
        post.increaseViewCount();
        return PostResponse.from(post);
    }

    public PostResponse updatePost(long id, String title, String content, PostCategory category, String tag) {
        Post post = findPostOrThrow(id);
        post.update(title, content, category, tag);
        return PostResponse.from(post);
    }

    private Post findPostOrThrow(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
    }

    public void deletePost(long id) {
        findPostOrThrow(id);
        repository.deleteById(id);
    }
}
