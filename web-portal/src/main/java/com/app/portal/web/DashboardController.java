package com.app.portal.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("roles", auth.getAuthorities());
        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        return "admin";
    }

    @GetMapping("/supervisor")
    public String supervisor(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        return "supervisor";
    }

    @GetMapping("/tecnico")
    public String tecnico(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        return "tecnico";
    }
}