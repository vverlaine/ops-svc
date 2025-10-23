package com.app.portal.controller;

import com.app.portal.session.CurrentUser;
import com.app.portal.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Suministra atributos globales disponibles en todas las vistas Thymeleaf.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    private final CurrentUser currentUser;

    public GlobalModelAttributes(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Expone al usuario autenticado actual como atributo `user`.
     */
    @ModelAttribute("user")
    public UserDto currentUser() {
        return currentUser.get();
    }

    /**
     * Expone la URI actual para facilitar resaltar elementos en la navegación.
     */
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request != null ? request.getRequestURI() : "";
    }
}
