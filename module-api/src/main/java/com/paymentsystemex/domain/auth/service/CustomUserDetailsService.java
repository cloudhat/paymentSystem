package com.paymentsystemex.domain.auth.service;


import com.paymentsystemex.global.auth.AuthenticationException;
import com.paymentsystemex.global.auth.userdetails.UserDetails;
import com.paymentsystemex.global.auth.userdetails.UserDetailsService;
import com.paymentsystemex.global.auth.userdetails.CustomUserDetails;
import core.domain.member.entity.Member;
import core.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Member member = memberRepository.findByEmail(username).orElseThrow(AuthenticationException::new);
        return new CustomUserDetails(member.getEmail(), member.getPassword(), member.getRole());
    }
}
