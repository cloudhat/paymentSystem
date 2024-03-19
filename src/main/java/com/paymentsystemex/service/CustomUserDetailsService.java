package com.paymentsystemex.service;


import com.paymentsystemex.auth.AuthenticationException;
import com.paymentsystemex.auth.userdetails.UserDetails;
import com.paymentsystemex.auth.userdetails.UserDetailsService;
import com.paymentsystemex.domain.member.CustomUserDetails;
import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.repository.MemberRepository;
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
