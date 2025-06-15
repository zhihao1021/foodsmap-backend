package com.nckueat.foodsmap.component.jwt;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final Long userId;
    private final String credentials;

    public JwtAuthenticationToken(String token) {
        super(null);
        this.userId = null;
        this.credentials = token;
        this.setAuthenticated(false);
    }

    public JwtAuthenticationToken(Long userId, String token,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        this.credentials = token;
        this.setAuthenticated(true);
    }

    @Override
    public String getCredentials() {
        return this.credentials;
    }

    @Override
    public Long getPrincipal() {
        return this.userId;
    }
}
