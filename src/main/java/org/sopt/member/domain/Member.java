package org.sopt.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.sopt.global.domain.BaseEntity;
import org.sopt.member.code.MemberErrorCode;
import org.sopt.member.exception.MemberException;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_member_email", columnNames = "email"))
public class Member extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'ROLE_USER'")
    private MemberRole role = MemberRole.ROLE_USER;

    protected Member() {}

    public Member(String name, String email, String password) {
        if (name == null || name.isBlank()) {
            throw new MemberException(MemberErrorCode.MEMBER_NAME_REQUIRED);
        }
        if (name.length() > 100) {
            throw new MemberException(MemberErrorCode.MEMBER_NAME_TOO_LONG);
        }
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static Member createSocial(String name, String email) {
        return new Member(name, email, null);
    }

    public MemberRole getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return this.name;
    }
}
