package com.app.portal.security;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RoleBasedSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String targetUrl = resolveTarget(authentication.getAuthorities());
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    private String resolveTarget(Collection<? extends GrantedAuthority> authorities) {
        // Orden de prioridad: ADMIN > SUPERVISOR > TECH > fallback
        for (GrantedAuthority ga : authorities) {
            String a = ga.getAuthority();
            if ("ROLE_ADMIN".equals(a))       return "/admin/users";
            if ("ROLE_SUPERVISOR".equals(a))  return "/supervisor/plan";
            if ("ROLE_TECH".equals(a))        return "/tech/today";
        }
        return "/dashboard";
    }
}