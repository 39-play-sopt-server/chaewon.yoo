package org.sopt.server.repository;

import org.springframework.stereotype.Repository;
import org.sopt.server.exception.PostNotFoundException;
import org.sopt.server.model.Post;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PostRepository {
    private final Map<Long, Post> posts = new HashMap<>();
    private long nextId = 1;

    public long createId() {
        return nextId++;
    }

    public void save(Post post) {
        posts.put(post.getId(), post);
    }

    public List<Post> findAll() {
        List<Long> ids = new ArrayList<>(posts.keySet());
        Collections.sort(ids);
        List<Post> result = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            result.add(posts.get(ids.get(i)));
        }
        return result;
    }

    public Post findById(long id) {
        Post post = posts.get(id);
        if (post == null) {
            throw new PostNotFoundException();
        }
        return post;
    }

    public void deleteById(long id) {
        findById(id);
        posts.remove(id);
    }
}
