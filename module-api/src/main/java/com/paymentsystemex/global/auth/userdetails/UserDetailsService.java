package com.paymentsystemex.global.auth.userdetails;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username);
}
