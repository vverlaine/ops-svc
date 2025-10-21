package com.app.portal.controller;

import com.app.portal.dto.PageDto;
import com.app.portal.dto.VisitDto;
import com.app.portal.dto.UserDto;
import com.app.portal.session.CurrentUser;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.app.portal.forms.CrearVisitaForm;
import com.app.portal.dto.CreateVisitCommand;
import com.app.portal.client.CustomerClient;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.time.ZoneOffset;
import java.time.OffsetDateTime;

@Controller
@RequiredArgsConstructor
public class VisitasController {

    private final RestTemplate restTemplate;
    private final CurrentUser current;
    private final CustomerClient customerClient;

    @Value("${visits.url}")
    private String visitsSvcUrl;

    @GetMapping("/visitas")
    public String listarVisitas(Model model) {
        UserDto user = current.get();

        if (user == null) {
            return "redirect:/login";
        }

        String url;
        if (Objects.equals(user.getRole(), "TECNICO")) {
            url = UriComponentsBuilder.fromHttpUrl(visitsSvcUrl + "/visits/me/today")
                    .queryParam("technicianId", user.getId())
                    .toUriString();
        } else if (Objects.equals(user.getRole(), "SUPERVISOR") || Objects.equals(user.getRole(), "ADMIN")) {
            url = visitsSvcUrl + "/visits";
        } else {
            return "error/403";
        }

        ResponseEntity<PageDto<VisitDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageDto<VisitDto>>() {
        }
        );

        List<VisitDto> visitas = response.getBody() != null ? response.getBody().getContent() : List.of();
        model.addAttribute("visitas", visitas);
        model.addAttribute("rol", user.getRole());
        return "visitas";
    }

    @GetMapping("/visits/crear")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("visita", new CrearVisitaForm());
        model.addAttribute("clientes", customerClient.listCustomers());
        return "visits/crear";
    }

    @PostMapping("/visits/crear")
    public String procesarFormularioCreacion(@ModelAttribute("visita") CrearVisitaForm form) {

        System.out.println("Customer ID: " + form.getCustomerId());
        System.out.println("Site ID: " + form.getSiteId());
        System.out.println("Technician ID: " + form.getTechnicianId());
        System.out.println("Priority: " + form.getPriority());
        System.out.println("Purpose: " + form.getPurpose());
        System.out.println("Notes: " + form.getNotesPlanned());

        OffsetDateTime start = form.getScheduledStartAt().atOffset(ZoneOffset.of("-06:00"));
        OffsetDateTime end = form.getScheduledEndAt().atOffset(ZoneOffset.of("-06:00"));

        System.out.println("start: " + start);
        System.out.println("end: " + end);

        Map<String, Object> body = new HashMap<>();
        body.put("customerId", form.getCustomerId());
        body.put("siteId", form.getSiteId());
        body.put("technicianId", form.getTechnicianId());
        body.put("scheduledStartAt", start.toString());
        body.put("scheduledEndAt", end.toString());
        body.put("priority", form.getPriority());
        body.put("purpose", form.getPurpose());
        body.put("notesPlanned", form.getNotesPlanned());

        String url = visitsSvcUrl + "/visits";
        restTemplate.postForObject(url, body, Void.class);
        return "redirect:/visitas";
    }
}
