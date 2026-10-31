package org.sopt.post.dto.response;

import org.sopt.post.domain.Post;
import org.sopt.member.domain.Member;
import java.time.LocalDateTime;

public record PostResponse(
        long id,
        Long memberId,
        String authorName,
        String title,
        String content,
        String category,
        String tag,
        LocalDateTime createdAt,
        long viewCount,
        long likeCount
) {
    public static PostResponse from(Post post, long likeCount) {
        Member member = post.getMember();
        Long memberId = null;
        String authorName = "작성자 정보 없음 또는 탈퇴한 회원";
        if (member != null) {
            memberId = member.getId();
            authorName = member.getName();
        }
        return new PostResponse(post.getId(), memberId, authorName,
                post.getTitle(), post.getContent(),
                post.getCategory().name(), post.getTag(), post.getCreatedAt(), post.getViewCount(), likeCount);
    }
}
