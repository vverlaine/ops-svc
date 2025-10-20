package com.app.portal.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/supervisor")
@PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN')")
public class SupervisorController {

    @GetMapping("/plan")
    public String verPlanificador() {
        return "supervisor/plan";
    }

    @PostMapping("/plan")
    @ResponseBody
    public String planificarVisitas() {
        // Lógica real irá aquí (llamar MS de planificación, etc.)
        return "OK: visitas planificadas";
    }
}