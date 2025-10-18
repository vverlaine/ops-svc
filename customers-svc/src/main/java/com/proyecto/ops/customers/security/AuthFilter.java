package com.proyecto.ops.customers.security;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private static final Set<String> PUBLIC = Set.of(
            "/actuator/health", "/actuator/info", "/error"
    );

    private final HttpClient http;

    public AuthFilter(HttpClient http) {
        this.http = http;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        final String path = req.getRequestURI();

        // Rutas públicas
        if (PUBLIC.stream().anyMatch(path::startsWith)) {
            chain.doFilter(req, res);
            return;
        }

        // Debe venir Authorization: Bearer xxx
        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json;charset=ISO-8859-1");
            res.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }

        // Valida con auth-svc /auth/me
        try {
            HttpRequest meReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8099/auth/me"))
                    .header(HttpHeaders.AUTHORIZATION, auth)
                    .GET()
                    .build();

            HttpResponse<String> meRes = http.send(meReq, HttpResponse.BodyHandlers.ofString());

            if (meRes.statusCode() != 200) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json;charset=ISO-8859-1");
                res.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }

            // Parseo súper simple sin libs extra (id, name, email, role)
            // Espera JSON: {"id":"...","name":"...","email":"...","role":"..."}
            String body = meRes.body();

            String id    = extract(body, "\"id\":\"", "\"");
            String name  = extract(body, "\"name\":\"", "\"");
            String email = extract(body, "\"email\":\"", "\"");
            String role  = extract(body, "\"role\":\"", "\"");

            AuthenticatedUser me = new AuthenticatedUser(UUID.fromString(id), name, email, role);

            // Deja al usuario en request para el ArgumentResolver
            req.setAttribute(CurrentUserArgumentResolver.REQ_ATTR, me);

            chain.doFilter(req, res);
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json;charset=ISO-8859-1");
            res.getWriter().write("{\"error\":\"Unauthorized\"}");
        }
    }

    private static String extract(String src, String start, String end) {
        int s = src.indexOf(start);
        if (s < 0) return null;
        s += start.length();
        int e = src.indexOf(end, s);
        if (e < 0) return null;
        return src.substring(s, e);
    }
}