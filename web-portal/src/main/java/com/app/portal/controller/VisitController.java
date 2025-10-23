package com.app.portal.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.portal.client.VisitClient;
import com.app.portal.dto.VisitDto;
import com.app.portal.session.CurrentUser;

/**
 * Controlador que ofrece vistas complementarias relacionadas con visitas,
 * independiente del listado principal.
 */
@Controller
public class VisitController {

    private final VisitClient visitClient;
    private final CurrentUser currentUser;

    public VisitController(VisitClient visitClient, CurrentUser currentUser) {
        this.visitClient = visitClient;
        this.currentUser = currentUser;
    }

    /**
     * Muestra la vista con las visitas programadas para el día del usuario autenticado.
     */
    @GetMapping("/visits/today")
    public String todayVisits(Model model) {
        if (!currentUser.isLoggedIn()) {
            return "redirect:/login";
        }

        var user = currentUser.get();
        List<VisitDto> visits = visitClient.myVisitsToday(user.getId());
        model.addAttribute("visits", visits);
        return "visits/today";
    }
}
