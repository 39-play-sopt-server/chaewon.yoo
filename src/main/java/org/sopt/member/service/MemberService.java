package org.sopt.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sopt.member.code.MemberErrorCode;
import org.sopt.member.domain.Member;
import org.sopt.member.dto.response.MemberResponse;
import org.sopt.member.exception.MemberException;
import org.sopt.member.repository.MemberRepository;
import org.sopt.post.repository.PostRepository;
import org.sopt.post.repository.PostLikeRepository;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public MemberService(MemberRepository memberRepository, PostRepository postRepository,
                         PostLikeRepository postLikeRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
    }

    public MemberResponse createMember(String name) {
        Member member = new Member(name);
        memberRepository.save(member);
        return MemberResponse.from(member);
    }

    public void deleteMember(long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        postRepository.findAllByMemberIdOrderByIdAsc(id).forEach(post -> post.removeAuthor());
        postRepository.flush();
        postLikeRepository.deleteAllByMemberId(id);
        postLikeRepository.flush();
        memberRepository.delete(member);
    }
}
