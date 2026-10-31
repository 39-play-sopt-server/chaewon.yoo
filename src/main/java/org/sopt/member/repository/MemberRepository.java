package org.sopt.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sopt.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
