package com.app.portal.client;

import com.app.portal.service.AuthClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Cliente REST para consultar supervisores desde el portal con mecanismos de fallback a auth-svc.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SupervisorClient {

    public record SupervisorOption(String id, String name, boolean active, String teamId) {}

    private final RestTemplate restTemplate;
    private final AuthClient authClient;

    @Value("${supervisors.url}")
    private String supervisorsSvcUrl;

    @Value("${supervisors.token:}")
    private String supervisorsSvcToken;

    /**
     * Recupera la lista de supervisores desde el servicio dedicado, utilizando token si fue configurado.
     * Aplica un fallback a auth-svc cuando el servicio principal no está disponible.
     */
    @SuppressWarnings("unchecked")
    public List<SupervisorOption> listSupervisors() {
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                    supervisorsSvcUrl + "/supervisors?size=200",
                    HttpMethod.GET,
                    authEntity(),
                    Map.class
            );
            Map<String, Object> response = responseEntity.getBody();
            if (response == null) {
                return Collections.emptyList();
            }
            Object content = response.get("content");
            if (!(content instanceof List<?> list)) {
                log.warn("Respuesta inesperada al listar supervisores: {}", response);
                return Collections.emptyList();
            }
            List<SupervisorOption> result = new ArrayList<>();
            for (Object entry : list) {
                if (entry instanceof Map<?, ?> map) {
                    Object idRaw = map.get("userId");
                    if (idRaw == null) continue;
                    String id = String.valueOf(idRaw);
                    if (id.isBlank()) continue;
                    Object nameRaw = map.containsKey("name") ? map.get("name") : map.get("userName");
                    String name = nameRaw == null ? "Sin nombre" : String.valueOf(nameRaw);
                    boolean active = Boolean.TRUE.equals(map.get("active"));
                    Object teamRaw = map.containsKey("teamId") ? map.get("teamId") : map.get("team_id");
                    String teamId = teamRaw == null ? null : String.valueOf(teamRaw);
                    result.add(new SupervisorOption(id, name, active, teamId));
                }
            }
            return result;
        } catch (HttpClientErrorException ex) {
            log.warn("Error al obtener supervisores desde supervisors-svc", ex);
            return fallbackFromAuth();
        } catch (RestClientException ex) {
            log.warn("Error al obtener supervisores desde supervisors-svc", ex);
            return fallbackFromAuth();
        }
    }

    /**
     * Obtiene supervisores desde auth-svc cuando el servicio especializado no responde.
     */
    private List<SupervisorOption> fallbackFromAuth() {
        try {
            return authClient.listUsers().stream()
                    .filter(map -> "SUPERVISOR".equalsIgnoreCase(String.valueOf(map.get("role"))))
                    .map(map -> {
                        Object idObj = map.get("id");
                        if (idObj == null) return null;
                        String id = String.valueOf(idObj);
                        if (id.isBlank()) return null;
                        Object nameRaw = map.containsKey("name") ? map.get("name") : "Sin nombre";
                        String name = nameRaw == null ? "Sin nombre" : String.valueOf(nameRaw);
                        Object teamRaw = map.get("teamId");
                        String teamId = teamRaw == null ? null : String.valueOf(teamRaw);
                        return new SupervisorOption(id, name, true, teamId);
                    })
                    .filter(opt -> opt != null)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error al obtener supervisores desde auth-svc", ex);
            return Collections.emptyList();
        }
    }

    /**
     * Construye la entidad HTTP con encabezados de autenticación opcionales para supervisors-svc.
     */
    private HttpEntity<Void> authEntity() {
        HttpHeaders headers = new HttpHeaders();
        if (supervisorsSvcToken != null && !supervisorsSvcToken.isBlank()) {
            String token = supervisorsSvcToken.trim();
            if (!token.toLowerCase().startsWith("bearer ") && !token.toLowerCase().startsWith("basic ")) {
                token = "Bearer " + token;
            }
            headers.set(HttpHeaders.AUTHORIZATION, token);
        }
        return new HttpEntity<>(headers);
    }
}
