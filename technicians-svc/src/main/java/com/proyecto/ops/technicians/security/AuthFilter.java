package com.proyecto.ops.technicians.security;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter {

  private final RestClient authRestClient;
  private static final Set<String> PUBLIC = Set.of("/actuator/health", "/actuator/info", "/error");

  public AuthFilter(@Qualifier("authRestClient") RestClient authRestClient) {
    this.authRestClient = authRestClient;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
    String path = request.getRequestURI();
    if (PUBLIC.contains(path)) { chain.doFilter(request, response); return; }

    String h = request.getHeader("Authorization");
    if (h == null || !h.startsWith("Bearer ")) { unauthorized(response); return; }

    String token = h.substring("Bearer ".length()).trim();
    try {
      @SuppressWarnings("unchecked")
      Map<String, Object> me = authRestClient.get()
          .uri("/auth/me")
          .header("Authorization", "Bearer " + token)
          .retrieve()
          .body(Map.class);

      AuthenticatedUser user = new AuthenticatedUser(
          UUID.fromString((String) me.get("id")),
          (String) me.get("name"),
          (String) me.get("email"),
          (String) me.get("role")
      );
      request.setAttribute(CurrentUserArgumentResolver.REQ_ATTR, user);
      chain.doFilter(request, response);
    } catch (RestClientResponseException ex) {
      unauthorized(response);
    }
  }

  private void unauthorized(HttpServletResponse res) throws IOException {
    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
    res.getWriter().write("{\"error\":\"Unauthorized\"}");
  }
}