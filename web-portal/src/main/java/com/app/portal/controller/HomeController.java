package com.app.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de rutas básicas que redirigen a la vista principal del portal.
 */
@Controller
public class HomeController {

    /**
     * Ruta raíz que apunta al dashboard.
     */
    @GetMapping("/")
    public String root() {
        return "dashboard";
    }

    /**
     * Alias en español para facilitar bookmarks hacia el dashboard.
     */
    @GetMapping("/inicio")
    public String inicio() {
        return "dashboard";
    }

    /**
     * Endpoint de easter egg que también responde con el dashboard.
     */
    @GetMapping("/skynet")
    public String skynet() {
        return "dashboard";
    }
}
