package org.sopt.server.repository;

import org.springframework.stereotype.Repository;
import org.sopt.server.exception.PostNotFoundException;
import org.sopt.server.model.Post;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PostRepository {
    private final Map<Long, Post> posts = new LinkedHashMap<>();
    private long nextId = 1;

    public long createId() {
        return nextId++;
    }

    public void save(Post post) {
        posts.put(post.getId(), post);
    }

    public List<Post> findAll() {
        return new ArrayList<>(posts.values());
    }

    public boolean existsById(long id) {
        return posts.containsKey(id);
    }

    public Post findById(long id) {
        Post post = posts.get(id);
        if (post == null) {
            throw new PostNotFoundException();
        }
        return post;
    }

    public void deleteById(long id) {
        if (!existsById(id)) {
            throw new PostNotFoundException();
        }
        posts.remove(id);
    }
}
