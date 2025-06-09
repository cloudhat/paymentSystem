package com.paymentsystemex.domain.auth.service;

import com.paymentsystemex.domain.auth.dto.TokenResponse;
import com.paymentsystemex.global.exception.AuthenticationException;
import com.paymentsystemex.global.auth.token.JwtTokenProvider;
import com.paymentsystemex.global.auth.userdetails.CustomUserDetails;
import com.paymentsystemex.global.auth.userdetails.UserDetails;
import core.domain.member.entity.Member;
import core.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public TokenResponse createToken(String email, String password) {
        Member member = memberRepository.findByEmail(email).orElseThrow(AuthenticationException::new);
        UserDetails userDetails = new CustomUserDetails(member.getEmail(), member.getPassword(), member.getRole());
        if (!userDetails.getPassword().equals(password)) {
            throw new AuthenticationException();
        }

        String token = jwtTokenProvider.createToken(userDetails.getUsername(), userDetails.getRole());

        return new TokenResponse(token);
    }

}
