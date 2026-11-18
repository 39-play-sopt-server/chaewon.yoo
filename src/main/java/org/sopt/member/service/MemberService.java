package org.sopt.member.service;

import org.springframework.stereotype.Service;
import java.util.List;
import org.sopt.auth.repository.TokenStore;
import org.sopt.auth.repository.SocialAccountRepository;
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
    private final TokenStore tokenStore;
    private final SocialAccountRepository socialAccountRepository;

    public MemberService(MemberRepository memberRepository, PostRepository postRepository,
                         PostLikeRepository postLikeRepository, TokenStore tokenStore,
                         SocialAccountRepository socialAccountRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.tokenStore = tokenStore;
        this.socialAccountRepository = socialAccountRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> readMembers() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::from)
                .toList();
    }

    public void deleteMember(long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        postRepository.clearAuthorByMemberId(id);
        postLikeRepository.deleteAllByMemberId(id);
        postLikeRepository.flush();
        socialAccountRepository.deleteAllByMemberId(id);
        memberRepository.delete(member);
        memberRepository.flush();
        tokenStore.deleteAllByMemberId(id);
    }
}
