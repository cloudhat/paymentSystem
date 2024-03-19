package com.paymentsystemex.auth.userdetails;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username);
}
