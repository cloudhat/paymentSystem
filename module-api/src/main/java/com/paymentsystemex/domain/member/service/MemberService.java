package com.paymentsystemex.domain.member.service;


import com.paymentsystemex.domain.member.entity.Member;
import com.paymentsystemex.domain.member.dto.MemberRequest;
import com.paymentsystemex.domain.member.dto.MemberResponse;
import com.paymentsystemex.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponse createMember(MemberRequest request) {
        Member member = memberRepository.save(request.toMember());
        return MemberResponse.of(member);
    }

}