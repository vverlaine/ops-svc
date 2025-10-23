package com.app.portal.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TechnicianClient {

    private final RestTemplate restTemplate;

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
            log.error("Error al obtener técnicos desde technicians-svc", ex);
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getTechnicianById(UUID id) {
        try {
            return restTemplate.getForObject(techniciansSvcUrl + "/technicians/" + id, Map.class);
        } catch (Exception ex) {
            log.error("Error al obtener técnico {}", id, ex);
            return null;
        }
    }
}
