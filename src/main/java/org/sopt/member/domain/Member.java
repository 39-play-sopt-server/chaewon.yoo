package org.sopt.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.sopt.global.domain.BaseEntity;
import org.sopt.member.code.MemberErrorCode;
import org.sopt.member.exception.MemberException;

@Entity
public class Member extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    protected Member() {}

    public Member(String name) {
        if (name == null || name.isBlank()) {
            throw new MemberException(MemberErrorCode.MEMBER_NAME_REQUIRED);
        }
        if (name.length() > 100) {
            throw new MemberException(MemberErrorCode.MEMBER_NAME_TOO_LONG);
        }
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
