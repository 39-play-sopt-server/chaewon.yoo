package org.sopt.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.sopt.post.domain.PostCategory;
import org.sopt.post.domain.Post;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Post p where p.id = :id")
    Optional<Post> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Post> findAllByMemberIdOrderByIdAsc(Long memberId);

    Page<Post> findByCategory(PostCategory category, Pageable pageable);

    @Query(value = """
            SELECT p.* FROM post p
            WHERE MATCH(p.title) AGAINST (:title IN NATURAL LANGUAGE MODE)
            ORDER BY p.created_at DESC, p.id DESC
            """, countQuery = """
            SELECT COUNT(*) FROM post p
            WHERE MATCH(p.title) AGAINST (:title IN NATURAL LANGUAGE MODE)
            """, nativeQuery = true)
    Page<Post> searchByTitle(@Param("title") String title, Pageable pageable);

    @Query(value = """
            SELECT p.* FROM post p
            WHERE p.category = :category
              AND MATCH(p.title) AGAINST (:title IN NATURAL LANGUAGE MODE)
            ORDER BY p.created_at DESC, p.id DESC
            """, countQuery = """
            SELECT COUNT(*) FROM post p
            WHERE p.category = :category
              AND MATCH(p.title) AGAINST (:title IN NATURAL LANGUAGE MODE)
            """, nativeQuery = true)
    Page<Post> searchByCategoryAndTitle(@Param("category") String category,
                                      @Param("title") String title, Pageable pageable);
}
