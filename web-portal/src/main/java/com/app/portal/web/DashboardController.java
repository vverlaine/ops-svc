package com.app.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.portal.visits.VisitsClient;

@Controller
public class DashboardController {

    private final VisitsClient visitsClient;

    public DashboardController(VisitsClient visitsClient) {
        this.visitsClient = visitsClient;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        String visitas = visitsClient.getVisitsStatus();
        model.addAttribute("visitas", visitas);
        return "dashboard";
    }
}