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

@Component
public class VisitClient {

    private final RestTemplate restTemplate;

    @Value("${visits.url:http://localhost:8090}")
    private String baseUrl;

    public VisitClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Para administradores — lista general
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

    // Para técnicos — solo sus visitas de hoy (por email)
    public List<VisitDto> myVisitsToday(UUID technicianId) {
        var url = baseUrl + "/visits/me/today?technicianId=" + technicianId;
        VisitDto[] response = restTemplate.getForObject(url, VisitDto[].class);
        return Arrays.asList(response);
    }
}
