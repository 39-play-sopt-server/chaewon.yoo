package org.sopt.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
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
            String keyword = title.strip();
            if (keyword.length() < 2) {
                throw new PostException(PostErrorCode.SEARCH_TITLE_TOO_SHORT);
            }
            PageRequest searchPageRequest = PageRequest.of(page, size);
            if (category == null) {
                result = repository.searchByTitle(keyword, searchPageRequest);
            } else {
                result = repository.searchByCategoryAndTitle(category.name(), keyword, searchPageRequest);
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
        return new PostPageResponse(
                result.getContent().stream().map(this::toPostResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext());
    }

    public PostResponse readPost(long id) {
        Post post = findPostOrThrow(id);
        post.increaseViewCount();
        return toPostResponse(post);
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
        Post post = findPostForUpdate(postId);
        Member member = findMemberOrThrow(memberId);
        if (postLikeRepository.existsByMemberIdAndPostId(memberId, postId)) {
            throw new PostException(PostErrorCode.POST_ALREADY_LIKED);
        }
        postLikeRepository.save(new PostLike(member, post));
    }

    public void cancelLike(long postId, Long memberId) {
        findPostForUpdate(postId);
        findMemberOrThrow(memberId);
        postLikeRepository.deleteByMemberIdAndPostId(memberId, postId);
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
