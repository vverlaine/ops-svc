package com.app.portal.controller;

import com.app.portal.session.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final CurrentUser current;

    public DashboardController(CurrentUser current) {
        this.current = current;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("user", current);
        return "dashboard";
    }
}