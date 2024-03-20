package com.paymentsystemex.service;

import com.paymentsystemex.auth.AuthenticationException;
import com.paymentsystemex.auth.token.JwtTokenProvider;
import com.paymentsystemex.auth.userdetails.UserDetails;
import com.paymentsystemex.auth.userdetails.UserDetailsService;
import com.paymentsystemex.dto.token.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;


    public TokenResponse createToken(String email, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (!userDetails.getPassword().equals(password)) {
            throw new AuthenticationException();
        }

        String token = jwtTokenProvider.createToken(userDetails.getUsername(), userDetails.getRole());

        return new TokenResponse(token);
    }

}
