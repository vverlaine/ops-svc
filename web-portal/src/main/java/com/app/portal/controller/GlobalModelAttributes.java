package com.app.portal.controller;

import com.app.portal.session.CurrentUser;
import com.app.portal.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final CurrentUser currentUser;

    public GlobalModelAttributes(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @ModelAttribute("user")
    public UserDto currentUser() {
        return currentUser.get();
    }

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request != null ? request.getRequestURI() : "";
    }
}
