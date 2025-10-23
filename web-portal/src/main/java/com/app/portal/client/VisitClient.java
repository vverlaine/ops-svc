package com.app.portal.client;

import com.app.portal.dto.VisitDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Cliente HTTP ligero para interactuar con el microservicio de visitas desde el portal.
 */
@Component
public class VisitClient {

    private final RestTemplate restTemplate;

    @Value("${visits.url:http://localhost:8090}")
    private String baseUrl;

    public VisitClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Recupera todas las visitas (uso administrativo).
     */
    public List<VisitDto> list() {
        var url = baseUrl + "/visits";
        ResponseEntity<List<VisitDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<VisitDto>>() {
        }
        );
        return response.getBody();
    }

    /**
     * Obtiene las visitas asignadas al técnico autenticado para el día actual.
     */
    public List<VisitDto> myVisitsToday(UUID technicianId) {
        var url = baseUrl + "/visits/me/today?technicianId=" + technicianId;
        VisitDto[] response = restTemplate.getForObject(url, VisitDto[].class);
        return Arrays.asList(response);
    }
}
