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

import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class VisitasController {

    private final RestTemplate restTemplate;
    private final CurrentUser current;

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
        return "visits/crear";
    }

    @PostMapping("/visits/crear")
    public String procesarFormularioCreacion(@ModelAttribute("visita") CrearVisitaForm form) {
        // Aquí hacés el POST al visits-svc
        CreateVisitCommand command = new CreateVisitCommand(
                form.getCustomerId(),
                form.getSiteId(),
                form.getTechnicianId(),
                form.getScheduledStartAt(),
                form.getScheduledEndAt()
        );

        String url = visitsSvcUrl + "/visits";
        restTemplate.postForObject(url, command, Void.class);
        return "redirect:/visitas";
    }
}
