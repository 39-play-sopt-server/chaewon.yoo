package org.sopt.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sopt.post.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
