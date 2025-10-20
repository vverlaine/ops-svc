package com.app.portal.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @GetMapping
    public String adminHome() {
        // Página principal de admin: /templates/admin/index.html
        return "admin/index";
    }

    @GetMapping("/users")
    public String users() {
        // Esta es la vista que fallaba: /templates/admin/users.html
        return "admin/users";
    }

    @GetMapping("/config")
    public String config() {
        // Si tienes /templates/fragments/config.html y quieres usarlo como página,
        // crea también /templates/admin/config.html o cambia este return.
        return "admin/config"; // crea templates/admin/config.html si aún no existe
    }
}