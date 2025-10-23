package com.app.portal.client;

import com.app.portal.service.AuthClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private List<Map<String, Object>> fallbackFromAuth() {
        try {
            List<Map<String, Object>> users = authClient.listUsers();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map<String, Object> user : users) {
                Object role = user.get("role");
                if (role != null && "TECNICO".equalsIgnoreCase(role.toString())) {
                    result.add(Map.of(
                            "id", user.get("id"),
                            "userName", user.getOrDefault("name", "Sin nombre"),
                            "active", Boolean.TRUE
                    ));
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Fallback de técnicos vía auth-svc falló", e);
            return Collections.emptyList();
        }
    }
}
