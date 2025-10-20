package com.app.portal.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class StatusService {

    private final WebClient webClient;

    public StatusService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public String ping(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(ex -> Mono.just("ERROR: " + ex.getMessage()))
                .block();
    }
}
