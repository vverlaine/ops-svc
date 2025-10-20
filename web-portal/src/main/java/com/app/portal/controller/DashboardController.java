package com.app.portal.controller;

import com.app.portal.session.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        CurrentUser u = (CurrentUser) session.getAttribute("user");
        model.addAttribute("user", u);
        return "dashboard";
    }
}