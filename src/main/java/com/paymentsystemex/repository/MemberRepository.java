package com.paymentsystemex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.paymentsystemex.domain.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}
