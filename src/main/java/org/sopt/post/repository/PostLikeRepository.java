package org.sopt.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sopt.post.domain.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByMemberIdAndPostId(Long memberId, Long postId);

    long countByPostId(Long postId);

    void deleteByMemberIdAndPostId(Long memberId, Long postId);

    void deleteAllByMemberId(Long memberId);

    void deleteAllByPostId(Long postId);
}
