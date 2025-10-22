package com.app.portal.config;

import com.app.portal.session.CurrentUser;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CurrentUserInterceptor implements HandlerInterceptor {
    private final CurrentUser currentUser;

    public CurrentUserInterceptor(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("user", currentUser.get());
        return true;
    }
}