package com.paymentsystemex.auth.principal;

public class AnonymousPrincipal extends UserPrincipal{

    public AnonymousPrincipal() {
        super(null, null);
    }
}
