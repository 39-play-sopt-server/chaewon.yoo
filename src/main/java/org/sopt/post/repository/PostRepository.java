package org.sopt.post.repository;

import org.springframework.stereotype.Repository;
import org.sopt.post.domain.Post;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public Optional<Post> findById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public void deleteById(long id) {
        posts.remove(id);
    }
}
