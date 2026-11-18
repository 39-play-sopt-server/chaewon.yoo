package org.sopt.auth.service;

import org.sopt.auth.domain.SocialAccount;
import org.sopt.auth.dto.response.SocialUserInfo;
import org.sopt.auth.repository.SocialAccountRepository;
import org.sopt.global.code.GlobalErrorCode;
import org.sopt.global.exception.BusinessException;
import org.sopt.member.domain.Member;
import org.sopt.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class SocialMemberService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;

    public SocialMemberService(MemberRepository memberRepository, SocialAccountRepository socialAccountRepository) {
        this.memberRepository = memberRepository;
        this.socialAccountRepository = socialAccountRepository;
    }

    public Long findOrCreate(SocialUserInfo userInfo) {
        Optional<SocialAccount> account = socialAccountRepository.findByProviderAndProviderUserId(
                userInfo.provider(), userInfo.providerUserId());
        if (account.isPresent()) {
            return account.get().getMember().getId();
        }
        if (memberRepository.existsByEmail(userInfo.email())) {
            throw new BusinessException(GlobalErrorCode.SOCIAL_EMAIL_CONFLICT);
        }
        Member member = memberRepository.save(Member.createSocial(userInfo.name(), userInfo.email()));
        socialAccountRepository.save(new SocialAccount(userInfo.provider(), userInfo.providerUserId(), member));
        return member.getId();
    }
}
