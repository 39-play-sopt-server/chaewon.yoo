package org.sopt.server.service;

import org.springframework.stereotype.Service;
import org.sopt.server.model.Category;
import org.sopt.server.model.Post;
import org.sopt.server.repository.PostRepository;
import java.util.List;

@Service
public class PostService {
    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public Post createPost(String title, String content, Category category, String tag) {
        long id = repository.createId();
        Post post = new Post(id, title, content, category, tag);
        repository.save(post);
        return post;
    }

    public List<Post> readPosts() {
        return repository.findAll();
    }

    public Post readPost(long id) {
        return repository.findById(id);
    }

    public Post updatePost(long id, String title, String content, Category category, String tag) {
        Post post = readPost(id);
        post.update(title, content, category, tag);
        return post;
    }

    public void deletePost(long id) {
        repository.deleteById(id);
    }
}
