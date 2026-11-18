package org.sopt.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.sopt.global.domain.BaseEntity;
import org.sopt.member.domain.Member;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_social_provider_user",
        columnNames = {"provider", "provider_user_id"}))
public class SocialAccount extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialProvider provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    protected SocialAccount() {}

    public SocialAccount(SocialProvider provider, String providerUserId, Member member) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.member = member;
    }

    public Member getMember() {
        return member;
    }
}
