package com.app.portal.web;

import com.app.portal.session.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {
    private final CurrentUser current;

    public AuthInterceptor(CurrentUser current) {
        this.current = current;
    }

    private boolean isPublic(String path) {
        return path.equals("/login") ||
               path.equals("/logout") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/assets/") ||
               path.equals("/health");
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String path = req.getRequestURI();
        if (isPublic(path)) return true;

        if (!current.isLoggedIn()) {
            res.sendRedirect("/login");
            return false;
        }
        return true;
    }
}