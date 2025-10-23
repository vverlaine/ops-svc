package com.app.portal.client;

import com.app.portal.service.AuthClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TechnicianClient {

    private final RestTemplate restTemplate;
    private final AuthClient authClient;

    @Value("${technicians.url}")
    private String techniciansSvcUrl;

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listTechnicians() {
        try {
            Map<String, Object> response = restTemplate.getForObject(
                    techniciansSvcUrl + "/technicians?size=200", Map.class);
            if (response == null) {
                return Collections.emptyList();
            }
            Object content = response.get("content");
            if (content instanceof List<?>) {
                return (List<Map<String, Object>>) content;
            }
            log.warn("Respuesta inesperada al listar técnicos: {}", response);
            return Collections.emptyList();
        } catch (Exception ex) {
            log.warn("Error al obtener técnicos desde technicians-svc", ex.getMessage());
            return fallbackFromAuth();
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getTechnicianById(UUID id) {
        try {
            return restTemplate.getForObject(techniciansSvcUrl + "/technicians/" + id, Map.class);
        } catch (Exception ex) {
            log.warn("Error al obtener técnico {} desde technicians-svc", id);
            return fallbackFromAuth().stream()
                    .filter(m -> id.toString().equals(m.get("id")))
                    .findFirst()
                    .orElse(null);
        }
    }

    public List<Map<String, Object>> listTechniciansForSupervisor(UUID supervisorId) {
        if (supervisorId == null) {
            return listTechnicians();
        }

        String supervisorKey = supervisorId.toString();
        List<Map<String, Object>> technicians = listTechnicians();
        if (technicians.isEmpty()) {
            return technicians;
        }

        Map<String, String> supervisorAssignments = null;
        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> tech : technicians) {
            if (tech == null) {
                continue;
            }

            String techId = extractId(tech);
            if (techId == null) {
                continue;
            }

            String supervisorFromPayload = extractSupervisorId(tech);
            if (supervisorFromPayload == null) {
                if (supervisorAssignments == null) {
                    supervisorAssignments = buildSupervisorAssignments();
                }
                supervisorFromPayload = supervisorAssignments.get(techId);
            }

            if (supervisorFromPayload != null && supervisorKey.equalsIgnoreCase(supervisorFromPayload)) {
                filtered.add(tech);
            }
        }

        return filtered;
    }

    private List<Map<String, Object>> fallbackFromAuth() {
        try {
            List<Map<String, Object>> users = authClient.listUsers();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map<String, Object> user : users) {
                Object role = user.get("role");
                if (role != null && "TECNICO".equalsIgnoreCase(role.toString())) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", user.get("id"));
                    row.put("userName", user.getOrDefault("name", "Sin nombre"));
                    row.put("active", Boolean.TRUE);
                    Object supervisorId = user.get("supervisorId");
                    if (supervisorId != null) {
                        row.put("supervisorId", supervisorId);
                    }
                    Object teamId = user.get("teamId");
                    if (teamId != null) {
                        row.put("teamId", teamId);
                    }
                    result.add(row);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Fallback de técnicos vía auth-svc falló", e);
            return Collections.emptyList();
        }
    }

    private Map<String, String> buildSupervisorAssignments() {
        Map<String, String> assignments = new HashMap<>();
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
                Object idObj = user.get("id");
                if (idObj == null) {
                    continue;
                }
                Object supervisorId = user.get("supervisorId");
                if (supervisorId != null) {
                    assignments.put(idObj.toString(), supervisorId.toString());
                }
            }
        } catch (Exception e) {
            log.warn("No se pudo construir el mapa de supervisores de técnicos", e);
        }
        return assignments;
    }

    private String extractSupervisorId(Map<String, Object> tech) {
        Object[] candidates = {
                tech.get("supervisorId"),
                tech.get("supervisor_id"),
                tech.get("teamId"),
                tech.get("team_id")
        };
        for (Object candidate : candidates) {
            if (candidate != null && !Objects.toString(candidate, "").isBlank()) {
                return candidate.toString();
            }
        }
        return null;
    }

    private String extractId(Map<String, Object> tech) {
        Object[] candidates = {
                tech.get("id"),
                tech.get("userId"),
                tech.get("user_id"),
                tech.get("technicianId"),
                tech.get("technician_id")
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
}
