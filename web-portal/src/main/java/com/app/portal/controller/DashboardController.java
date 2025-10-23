package com.app.portal.controller;

import com.app.portal.session.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.app.portal.dto.UserDto;

/**
 * Controlador encargado de renderizar el tablero principal del portal.
 */
@Controller
public class DashboardController {

    private final CurrentUser current;

    public DashboardController(CurrentUser current) {
        this.current = current;
    }

    /**
     * Carga la vista de dashboard con la información del usuario autenticado.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        UserDto user = current.get();

        model.addAttribute("user", user);
        model.addAttribute("user", current.get());
        return "dashboard";
    }
}
