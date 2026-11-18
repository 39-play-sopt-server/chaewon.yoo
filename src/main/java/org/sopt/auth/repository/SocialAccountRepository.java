package org.sopt.auth.repository;

import org.sopt.auth.domain.SocialAccount;
import org.sopt.auth.domain.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderUserId(SocialProvider provider, String providerUserId);

    @Modifying(flushAutomatically = true)
    @Query("delete from SocialAccount s where s.member.id = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
