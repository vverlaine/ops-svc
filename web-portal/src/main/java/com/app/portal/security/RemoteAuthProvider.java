package com.app.portal.security;

import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.app.portal.auth.AuthClient;
import com.app.portal.auth.LoginResponse;

@Component
public class RemoteAuthProvider implements AuthenticationProvider {

    private final AuthClient auth;

    public RemoteAuthProvider(AuthClient auth) {
        this.auth = auth;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        LoginResponse res = auth.login(email, password);
        if (res == null || res.token() == null) {
            throw new BadCredentialsException("Credenciales inválidas o error al conectar con auth-svc");
        }

        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + res.role()));

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(email, null, authorities);
        token.setDetails(Map.of(
                "jwt", res.token(),
                "name", res.name(),
                "role", res.role()
        ));

        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}