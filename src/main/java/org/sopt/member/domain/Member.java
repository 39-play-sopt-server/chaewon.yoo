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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

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

    public Member(String name, String email, String password) {
        this(name);
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return this.name;
    }
}
