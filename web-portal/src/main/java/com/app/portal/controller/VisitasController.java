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
import com.app.portal.client.CustomerClient;
import com.app.portal.client.TechnicianClient;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.time.ZoneOffset;
import java.time.OffsetDateTime;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class VisitasController {

    private final RestTemplate restTemplate;
    private final CurrentUser current;
    private final CustomerClient customerClient;
    private final TechnicianClient technicianClient;

    @Value("${visits.url}")
    private String visitsSvcUrl;

    @GetMapping("/visitas")
    public String listarVisitas(
            @RequestParam(required = false) String estado,
            Model model
    ) {
        UserDto user = current.get();

        if (user == null) {
            return "redirect:/login";
        }

        List<VisitDto> visitas;
        String url;

        if (Objects.equals(user.getRole(), "TECNICO")) {
            OffsetDateTime from = OffsetDateTime.now(ZoneOffset.UTC).minusDays(7);
            OffsetDateTime to = OffsetDateTime.now(ZoneOffset.UTC).plusDays(30);
            url = UriComponentsBuilder.fromHttpUrl(visitsSvcUrl + "/visits")
                    .queryParam("technicianId", user.getId())
                    .queryParam("from", from)
                    .queryParam("to", to)
                    .queryParam("page", 0)
                    .queryParam("size", 100)
                    .toUriString();

            ResponseEntity<PageDto<VisitDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<PageDto<VisitDto>>() {
            }
            );
            visitas = response.getBody() != null ? response.getBody().getContent() : List.of();

        } else if (Objects.equals(user.getRole(), "SUPERVISOR") || Objects.equals(user.getRole(), "ADMIN")) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(visitsSvcUrl + "/visits");
            if (estado != null && !estado.isEmpty()) {
                builder.queryParam("state", estado);
            }
            url = builder.toUriString();

            ResponseEntity<PageDto<VisitDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<PageDto<VisitDto>>() {
            }
            );
            visitas = response.getBody() != null ? response.getBody().getContent() : List.of();
        } else {
            return "error/403";
        }

        Map<String, String> technicianNames = new HashMap<>();
        for (VisitDto visita : visitas) {
            try {
                Map<String, Object> cliente = customerClient.getCustomerById(UUID.fromString(visita.getCustomerId()));
                if (cliente == null || cliente.isEmpty()) {
                    visita.setCustomerName("Cliente no disponible");
                } else {
                    visita.setCustomerName((String) cliente.getOrDefault("name", "Desconocido"));
                }
            } catch (Exception e) {
                visita.setCustomerName("Error cargando cliente");
            }

            String technicianId = visita.getTechnicianId();
            if (technicianId == null || technicianId.isBlank()) {
                visita.setTechnicianName("Sin técnico");
                continue;
            }

            String cachedName = technicianNames.get(technicianId);
            if (cachedName != null) {
                visita.setTechnicianName(cachedName);
                continue;
            }

            try {
                Map<String, Object> tecnico = technicianClient.getTechnicianById(UUID.fromString(technicianId));
                String name = tecnico != null ? (String) tecnico.getOrDefault("userName", "Sin técnico") : "Sin técnico";
                if (name == null || name.isBlank()) {
                    name = "Sin técnico";
                }
                technicianNames.put(technicianId, name);
                visita.setTechnicianName(name);
            } catch (Exception ex) {
                visita.setTechnicianName("Error técnico");
            }
        }

        model.addAttribute("visitas", visitas);
        model.addAttribute("rol", user.getRole());
        return "visitas";
    }

    @GetMapping("/visits/crear")
    public String mostrarFormularioCreacion(Model model) {
        CrearVisitaForm form = new CrearVisitaForm();
        form.setScheduledStartDate(LocalDate.now());
        model.addAttribute("visita", form);
        model.addAttribute("clientes", customerClient.listCustomers());
        model.addAttribute("tecnicos", technicianClient.listTechnicians());
        return "visits/crear";
    }

    @PostMapping("/visits/crear")
    public String procesarFormularioCreacion(@ModelAttribute("visita") CrearVisitaForm form) {
        var startLocal = form.getScheduledStartAt();
        if (startLocal == null && form.getScheduledStartDate() != null) {
            startLocal = form.getScheduledStartDate().atStartOfDay();
        }
        if (startLocal == null) {
            startLocal = OffsetDateTime.now(ZoneOffset.of("-06:00")).toLocalDateTime();
        }

        var endLocal = form.getScheduledEndAt();
        if (endLocal == null) {
            endLocal = startLocal.plusHours(2);
        }

        OffsetDateTime start = startLocal.atOffset(ZoneOffset.of("-06:00"));
        OffsetDateTime end = endLocal.atOffset(ZoneOffset.of("-06:00"));

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

    @GetMapping("/visitas/{id}")
    public String verVisita(@PathVariable String id, Model model) {
        // Obtener la visita desde visits-svc
        String url = visitsSvcUrl + "/visits/" + id;
        VisitDto visita = restTemplate.getForObject(url, VisitDto.class);

        // Obtener nombre del cliente (si aplica)
        try {
            Map<String, Object> cliente = customerClient.getCustomerById(UUID.fromString(visita.getCustomerId()));
            visita.setCustomerName((String) cliente.getOrDefault("name", "Desconocido"));
        } catch (Exception e) {
            visita.setCustomerName("Error cargando cliente");
        }

        if (visita.getTechnicianId() != null && !visita.getTechnicianId().isBlank()) {
            try {
                Map<String, Object> tecnico = technicianClient.getTechnicianById(UUID.fromString(visita.getTechnicianId()));
                String name = tecnico != null ? (String) tecnico.getOrDefault("userName", "Sin técnico") : "Sin técnico";
                visita.setTechnicianName((name == null || name.isBlank()) ? "Sin técnico" : name);
            } catch (Exception ex) {
                visita.setTechnicianName("Error técnico");
            }
        } else {
            visita.setTechnicianName("Sin técnico");
        }

        model.addAttribute("visita", visita);
        model.addAttribute("esTecnico", current.isLoggedIn() && Objects.equals(current.getRole(), "TECNICO"));
        return "visits/ver"; // <-- este es el HTML que debes crear
    }

    @PostMapping("/visits/{id}/update")
    public String actualizarVisita(
            @PathVariable String id,
            @RequestParam String estado,
            @RequestParam String proposito,
            @RequestParam String notas
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("state", estado);
        body.put("purpose", proposito);
        body.put("notesPlanned", notas);

        String url = visitsSvcUrl + "/visits/" + id;
        restTemplate.patchForObject(url, body, Void.class);

        return "redirect:/visitas";
    }

    @GetMapping("/visitas/{id}/checkin")
    public String checkIn(@PathVariable String id) {
        UserDto user = current.get();
        if (user == null) {
            return "redirect:/login";
        }
        Map<String, Object> body = new HashMap<>();
        body.put("actorId", user.getId());
        body.put("when", OffsetDateTime.now(ZoneOffset.UTC).toString());
        String url = visitsSvcUrl + "/visits/" + id + "/check-in";
        restTemplate.postForObject(url, body, VisitDto.class);
        return "redirect:/visitas";
    }

    @GetMapping("/visitas/{id}/complete")
    public String checkOut(@PathVariable String id) {
        UserDto user = current.get();
        if (user == null) {
            return "redirect:/login";
        }
        Map<String, Object> body = new HashMap<>();
        body.put("actorId", user.getId());
        body.put("when", OffsetDateTime.now(ZoneOffset.UTC).toString());
        body.put("workSummary", "");
        String url = visitsSvcUrl + "/visits/" + id + "/check-out";
        restTemplate.postForObject(url, body, VisitDto.class);
        return "redirect:/visitas";
    }
}
