package org.sopt.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.sopt.post.dto.response.PostResponse;
import org.sopt.post.dto.response.PostPageResponse;
import org.sopt.post.domain.PostCategory;
import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostLike;
import org.sopt.post.repository.PostLikeRepository;
import org.sopt.post.repository.PostRepository;
import org.sopt.post.exception.PostException;
import org.sopt.post.code.PostErrorCode;
import org.sopt.member.domain.Member;
import org.sopt.member.repository.MemberRepository;
import org.sopt.member.code.MemberErrorCode;
import org.sopt.member.exception.MemberException;

@Service
@Transactional
public class PostService {
    private final PostRepository repository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;

    public PostService(PostRepository repository, MemberRepository memberRepository,
                       PostLikeRepository postLikeRepository) {
        this.repository = repository;
        this.memberRepository = memberRepository;
        this.postLikeRepository = postLikeRepository;
    }

    public PostResponse createPost(Long memberId, String title, String content, PostCategory category, String tag) {
        Member member = findMemberOrThrow(memberId);
        Post post = new Post(member, title, content, category, tag);
        repository.save(post);
        return toPostResponse(post);
    }

    @Transactional(readOnly = true)
    public PostPageResponse readPosts(int page, int size, Long memberId, PostCategory category) {
        PageRequest pageRequest = createPageRequest(page, size);
        Specification<Post> filter = (root, query, builder) -> {
            List<Predicate> conditions = new ArrayList<>();
            if (memberId != null) {
                conditions.add(builder.equal(root.get("member").get("id"), memberId));
            }
            if (category != null) {
                conditions.add(builder.equal(root.get("category"), category));
            }
            return builder.and(conditions.toArray(new Predicate[0]));
        };
        Page<Post> result = repository.findAll(filter, pageRequest);
        return toPageResponse(result);
    }

    @Transactional(readOnly = true)
    public PostPageResponse searchPosts(int page, int size, PostCategory category, String title) {
        PageRequest pageRequest = createPageRequest(page, size);
        Page<Post> result;
        if (title == null || title.isBlank()) {
            if (category == null) {
                result = repository.findAll(pageRequest);
            } else {
                result = repository.findByCategory(category, pageRequest);
            }
        } else {
            String keyword = title.replace('"', ' ').strip();
            if (keyword.length() < 2) {
                throw new PostException(PostErrorCode.SEARCH_TITLE_TOO_SHORT);
            }
            String phrase = "\"" + keyword + "\"";
            PageRequest searchPageRequest = PageRequest.of(page, size);
            if (category == null) {
                result = repository.searchByTitle(phrase, searchPageRequest);
            } else {
                result = repository.searchByCategoryAndTitle(category.name(), phrase, searchPageRequest);
            }
        }

        return toPageResponse(result);
    }

    private PageRequest createPageRequest(int page, int size) {
        if (page < 0) {
            throw new PostException(PostErrorCode.INVALID_PAGE);
        }
        if (size < 1) {
            throw new PostException(PostErrorCode.INVALID_PAGE_SIZE);
        }
        return PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt", "id"));
    }

    private PostPageResponse toPageResponse(Page<Post> result) {
        List<Long> postIds = result.getContent().stream().map(Post::getId).toList();
        Map<Long, Long> likeCounts = new HashMap<>();
        if (!postIds.isEmpty()) {
            repository.findAllWithAuthorByIdIn(postIds);
            postLikeRepository.countByPostIds(postIds).forEach(count ->
                    likeCounts.put(count.getPostId(), count.getLikeCount()));
        }
        return new PostPageResponse(
                result.getContent().stream()
                        .map(post -> PostResponse.from(post, likeCounts.getOrDefault(post.getId(), 0L)))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext());
    }

    public PostResponse readPost(long id) {
        if (repository.incrementViewCount(id) == 0) {
            throw new PostException(PostErrorCode.POST_NOT_FOUND);
        }
        return toPostResponse(findPostOrThrow(id));
    }

    public PostResponse updatePost(long id, String title, String content, PostCategory category, String tag) {
        Post post = findPostOrThrow(id);
        post.update(title, content, category, tag);
        return toPostResponse(post);
    }

    private Post findPostOrThrow(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
    }

    private PostResponse toPostResponse(Post post) {
        long likeCount = postLikeRepository.countByPostId(post.getId());
        return PostResponse.from(post, likeCount);
    }

    public void likePost(long postId, Long memberId) {
        Member member = findMemberOrThrow(memberId);
        Post post = findPostForShare(postId);
        try {
            postLikeRepository.saveAndFlush(new PostLike(member, post));
        } catch (DataIntegrityViolationException exception) {
            for (Throwable cause = exception.getCause(); cause != null; cause = cause.getCause()) {
                if (cause instanceof ConstraintViolationException violation) {
                    String name = violation.getConstraintName();
                    if ("uk_post_like_member_post".equals(name)
                            || (name != null && name.endsWith(".uk_post_like_member_post"))) {
                        throw new PostException(PostErrorCode.POST_ALREADY_LIKED);
                    }
                }
            }
            throw exception;
        }
    }

    public void cancelLike(long postId, Long memberId) {
        findMemberOrThrow(memberId);
        findPostForShare(postId);
        postLikeRepository.deleteByMemberIdAndPostId(memberId, postId);
    }

    private Post findPostForShare(long id) {
        return repository.findByIdForShare(id)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
    }

    private Post findPostForUpdate(long id) {
        return repository.findByIdForUpdate(id)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
    }

    private Member findMemberOrThrow(Long memberId) {
        if (memberId == null) {
            throw new MemberException(MemberErrorCode.MEMBER_ID_REQUIRED);
        }
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public void deletePost(long id) {
        findPostForUpdate(id);
        postLikeRepository.deleteAllByPostId(id);
        postLikeRepository.flush();
        repository.deleteById(id);
    }
}
