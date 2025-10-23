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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.portal.forms.CrearVisitaForm;
import com.app.portal.client.CustomerClient;
import com.app.portal.client.TechnicianClient;
import com.app.portal.client.SupervisorClient;
import com.app.portal.service.AuthClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
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
    private final SupervisorClient supervisorClient;
    private final AuthClient authClient;

    @Value("${visits.url}")
    private String visitsSvcUrl;

    @GetMapping("/visitas")
    public String listarVisitas(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String prioridad,
            Model model
    ) {
        UserDto user = current.get();

        if (user == null) {
            return "redirect:/login";
        }

        List<VisitDto> visitas;
        String url;

        String role = user.getRole();

        if (Objects.equals(role, "TECNICO")) {
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

        } else if (Objects.equals(role, "SUPERVISOR")) {
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
            Map<String, String> allowedTechnicians = technicianIdsForSupervisor(user);
        visitas = visitas.stream()
                .filter(v -> allowedTechnicians.containsKey(normalizeId(v.getTechnicianId())))
                .collect(Collectors.toList());
        } else if (Objects.equals(role, "ADMIN")) {
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

        if (prioridad != null && !prioridad.isBlank()) {
            String prioridadUpper = prioridad.trim().toUpperCase();
            visitas = visitas.stream()
                    .filter(v -> {
                        String visitPriority = v.getPriority();
                        return visitPriority != null && visitPriority.trim().equalsIgnoreCase(prioridadUpper);
                    })
                    .toList();
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
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("prioridadSeleccionada", prioridad);
        return "visitas";
    }

    @GetMapping("/visits/crear")
    public String mostrarFormularioCreacion(Model model) {
        UserDto user = current.get();
        if (user == null) {
            return "redirect:/login";
        }
        CrearVisitaForm form = new CrearVisitaForm();
        form.setScheduledStartDate(LocalDate.now());
        model.addAttribute("visita", form);
        model.addAttribute("clientes", customerClient.listCustomers());
        List<Map<String, Object>> tecnicosDisponibles = techniciansAvailableFor(user);
        model.addAttribute("tecnicos", tecnicosDisponibles);
        model.addAttribute("sinTecnicos", tecnicosDisponibles.isEmpty());
        return "visits/crear";
    }

    @PostMapping("/visits/crear")
    public String procesarFormularioCreacion(
            @ModelAttribute("visita") CrearVisitaForm form,
            RedirectAttributes redirectAttributes
    ) {
        UserDto user = current.get();
        if (user == null) {
            return "redirect:/login";
        }
        if (Objects.equals(user.getRole(), "SUPERVISOR")) {
            Map<String, String> allowedTechnicians = technicianIdsForSupervisor(user);
            String requestedTechnician = normalizeId(form.getTechnicianId());
            if (requestedTechnician == null || !allowedTechnicians.containsKey(requestedTechnician)) {
                redirectAttributes.addFlashAttribute("error", "No puedes asignar un técnico fuera de tu equipo.");
                return "redirect:/visits/crear";
            }
        }
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
        String siteId = form.getSiteId();
        if (siteId == null || siteId.isBlank()) {
            siteId = form.getCustomerId();
        }
        body.put("siteId", siteId);
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
        UserDto user = current.get();
        if (user == null) {
            return "redirect:/login";
        }
        // Obtener la visita desde visits-svc
        String url = visitsSvcUrl + "/visits/" + id;
        VisitDto visita = restTemplate.getForObject(url, VisitDto.class);

        if (Objects.equals(user.getRole(), "SUPERVISOR")) {
            Map<String, String> allowedTechnicians = technicianIdsForSupervisor(user);
            if (!allowedTechnicians.containsKey(normalizeId(visita.getTechnicianId()))) {
                return "error/403";
            }
        }

        // Obtener nombre del cliente (si aplica)
        try {
            Map<String, Object> cliente = customerClient.getCustomerById(UUID.fromString(visita.getCustomerId()));
            visita.setCustomerName((String) cliente.getOrDefault("name", "Desconocido"));
            Object rawAddress = cliente.get("address");
            visita.setCustomerAddress(rawAddress != null ? rawAddress.toString() : null);
        } catch (Exception e) {
            visita.setCustomerName("Error cargando cliente");
            visita.setCustomerAddress(null);
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

    private List<Map<String, Object>> techniciansAvailableFor(UserDto user) {
        if (user == null) {
            return technicianClient.listTechnicians();
        }
        if (Objects.equals(user.getRole(), "SUPERVISOR")) {
            Map<String, String> allowed = technicianIdsForSupervisor(user);
            if (allowed.isEmpty()) {
                return List.of();
            }
            List<Map<String, Object>> allTechnicians = technicianClient.listTechnicians();
            List<Map<String, Object>> filtered = new ArrayList<>();
            for (Map<String, Object> technician : allTechnicians) {
                String techId = extractTechnicianId(technician);
                if (techId != null && allowed.containsKey(normalizeId(techId))) {
                    Map<String, Object> enriched = new HashMap<>(technician);
                    enriched.put("id", techId);
                    enriched.put("userId", techId);
                    filtered.add(enriched);
                }
            }
            return filtered;
        }
        return technicianClient.listTechnicians();
    }

    private Map<String, String> technicianIdsForSupervisor(UserDto supervisor) {
        if (supervisor == null || supervisor.getId() == null) {
            return Map.of();
        }
        String supervisorKey = normalizeId(supervisor.getId().toString());
        String supervisorTeamNormalized = null;
        try {
            var supervisors = supervisorClient.listSupervisors();
            for (var opt : supervisors) {
                if (opt == null || opt.id() == null) continue;
                if (opt.id().equalsIgnoreCase(supervisor.getId().toString())) {
                    if (opt.teamId() != null) {
                        supervisorTeamNormalized = normalizeId(opt.teamId());
                    }
                    break;
                }
            }
        } catch (Exception e) {
            // sin supervisor client seguimos intentando con el propio id
        }
        if (supervisorTeamNormalized == null && supervisor.getTeamId() != null) {
            supervisorTeamNormalized = normalizeId(supervisor.getTeamId());
        }
        if (supervisorTeamNormalized == null) {
            supervisorTeamNormalized = supervisorKey;
        }
        Map<String, String> ids = new HashMap<>();
        try {
            List<Map<String, Object>> users = authClient.listUsers();
            for (Map<String, Object> user : users) {
                if (user == null) {
                    continue;
                }
                Object role = user.get("role");
                if (role == null || !"TECNICO".equalsIgnoreCase(role.toString())) {
                    continue;
                }
                Object supervisorId = firstNonNull(user.get("supervisorId"), user.get("supervisor_id"));
                Object teamId = firstNonNull(user.get("teamId"), user.get("team_id"));

                boolean matches = false;
                if (supervisorId != null && supervisorKey.equals(normalizeId(supervisorId.toString()))) {
                    matches = true;
                } else if (teamId != null && supervisorTeamNormalized != null
                        && supervisorTeamNormalized.equals(normalizeId(teamId.toString()))) {
                    matches = true;
                }
                if (!matches) {
                    continue;
                }

                Object idObj = user.get("id");
                if (idObj == null) {
                    continue;
                }
                String normalized = normalizeId(idObj.toString());
                if (normalized != null && !normalized.isBlank()) {
                    ids.put(normalized, idObj.toString());
                }
            }
        } catch (Exception ex) {
            // Si no se puede contactar auth-svc, retorna conjunto vacío
        }
        if (ids.isEmpty()) {
            try {
                List<Map<String, Object>> technicians = technicianClient.listTechnicians();
                for (Map<String, Object> tech : technicians) {
                    String techId = extractTechnicianId(tech);
                    if (techId == null) {
                        continue;
                    }
                    String normalizedTechId = normalizeId(techId);
                    String supervisorFromPayload = extractSupervisorId(tech);
                    if (normalizedTechId == null || normalizedTechId.isBlank()) {
                        continue;
                    }
                    if (supervisorFromPayload != null && supervisorKey.equals(normalizeId(supervisorFromPayload))) {
                        ids.putIfAbsent(normalizedTechId, techId);
                        continue;
                    }
                    Object teamCandidate = firstNonNull(tech.get("teamId"), tech.get("team_id"));
                    if (teamCandidate != null && supervisorTeamNormalized != null
                            && supervisorTeamNormalized.equals(normalizeId(teamCandidate.toString()))) {
                        ids.putIfAbsent(normalizedTechId, techId);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return ids;
    }

    private String extractTechnicianId(Map<String, Object> technician) {
        if (technician == null) {
            return null;
        }
        Object[] candidates = {
                technician.get("id"),
                technician.get("userId"),
                technician.get("user_id"),
                technician.get("technicianId"),
                technician.get("technician_id")
        };
        for (Object candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            String id = candidate.toString();
            if (!id.isBlank()) {
                return id;
            }
        }
        return null;
    }

    private String extractSupervisorId(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        Object[] candidates = {
                map.get("supervisorId"),
                map.get("supervisor_id"),
                map.get("teamId"),
                map.get("team_id")
        };
        for (Object candidate : candidates) {
            if (candidate != null && !Objects.toString(candidate, "").isBlank()) {
                return candidate.toString();
            }
        }
        return null;
    }

    private String normalizeId(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toLowerCase();
    }

    private Object firstNonNull(Object... values) {
        for (Object value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
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
