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

        String supervisorKey = normalize(supervisorId.toString());
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

            String technicianUserId = extractUserId(tech);
            if (technicianUserId == null) {
                continue;
            }

            String supervisorFromPayload = extractSupervisorId(tech);
            if (supervisorFromPayload == null) {
                if (supervisorAssignments == null) {
                    supervisorAssignments = buildSupervisorAssignments();
                }
                supervisorFromPayload = supervisorAssignments.get(normalize(technicianUserId));
            }

            if (supervisorFromPayload != null && supervisorKey.equals(normalize(supervisorFromPayload))) {
                Map<String, Object> enriched = new HashMap<>(tech);
                enriched.put("userId", technicianUserId);
                enriched.put("id", technicianUserId);
                filtered.add(enriched);
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
                    copyIfPresent(row, user, "supervisorId");
                    copyIfPresent(row, user, "teamId");
                    copyIfPresent(row, user, "supervisor_id");
                    copyIfPresent(row, user, "team_id");
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
                Object supervisorId = firstNonNull(user.get("supervisorId"), user.get("supervisor_id"));
                Object teamId = firstNonNull(user.get("teamId"), user.get("team_id"));

                String normalizedId = normalize(idObj.toString());

                if (supervisorId != null) {
                    assignments.put(normalizedId, supervisorId.toString());
                } else if (teamId != null) {
                    assignments.put(normalizedId, teamId.toString());
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

    private String extractUserId(Map<String, Object> tech) {
        Object[] candidates = {
                tech.get("userId"),
                tech.get("user_id"),
                tech.get("id"),
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

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toLowerCase();
    }

    private void copyIfPresent(Map<String, Object> target, Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (value != null) {
            target.put(key, value);
        }
    }

    private Object firstNonNull(Object... values) {
        for (Object value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
