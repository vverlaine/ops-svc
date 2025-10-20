package com.app.portal.visits;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class VisitsClient {

    private final WebClient webClient;
    private final String baseUrl;

    public VisitsClient(WebClient.Builder builder,
                        @Value("${gateway.base-url}") String baseUrl) {
        this.webClient = builder.build();
        this.baseUrl = baseUrl;
    }

    public String getVisitsStatus() {
        String token = resolveToken();
        if (token == null || token.isBlank()) {
            return "Error: no hay JWT en el contexto (haz login otra vez).";
        }

        return webClient.get()
                .uri(baseUrl + "/visits/status")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(ex -> Mono.just("Error al obtener visitas: " + ex.getMessage()))
                .block();
    }

    @SuppressWarnings("unchecked")
    private String resolveToken() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object details = auth.getDetails();
        if (details instanceof String s) {
            return s;
        }
        if (details instanceof Map<?, ?> m) {
            Object v = m.get("jwt");
            return (v instanceof String) ? (String) v : null;
        }
        return null;
    }
}