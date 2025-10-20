package com.app.portal.auth;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class AuthClient {

    private final WebClient web;
    private final String base;
    private final String loginPath;

    public AuthClient(WebClient web, Environment env) {
        this.web = web;
        this.base = env.getProperty("auth.base-url", "http://localhost:8081/auth");
        this.loginPath = env.getProperty("auth.login-path", "/login");
    }

    public LoginResponse login(String email, String password) {
        try {
            return web.post()
                    .uri(base + loginPath)
                    .bodyValue(new LoginRequest(email, password))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, resp -> {
                        System.err.println("❌ Credenciales inválidas o usuario no encontrado");
                        return resp.createException();
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, resp -> {
                        System.err.println("💥 Error en el servidor de autenticación");
                        return resp.createException();
                    })
                    .bodyToMono(LoginResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println("⚠️ Error HTTP: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.err.println("🚨 Error inesperado: " + e.getMessage());
            return null;
        }
    }
}