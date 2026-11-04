package org.sopt.post.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sopt.post.domain.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    interface PostLikeCount {
        Long getPostId();
        long getLikeCount();
    }

    long countByPostId(Long postId);

    @Query("""
            select l.post.id as postId, count(l) as likeCount
            from PostLike l where l.post.id in :postIds group by l.post.id
            """)
    List<PostLikeCount> countByPostIds(@Param("postIds") Collection<Long> postIds);

    @Modifying
    @Query("delete from PostLike l where l.member.id = :memberId and l.post.id = :postId")
    void deleteByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    void deleteAllByMemberId(Long memberId);

    void deleteAllByPostId(Long postId);
}
