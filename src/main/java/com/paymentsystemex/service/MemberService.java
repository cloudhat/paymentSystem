package com.paymentsystemex.service;


import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.dto.member.MemberRequest;
import com.paymentsystemex.dto.member.MemberResponse;
import com.paymentsystemex.repository.MemberRepository;
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