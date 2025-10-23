/*
 * -----------------------------------------------------------------------------
 * WebConfig.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase de configuración global de la capa web del microservicio "supervisors-svc".
 *   Registra filtros y resolvers personalizados que se aplican a todas las solicitudes HTTP.
 *
 * Contexto de uso:
 *   - Gestiona la autenticación y el contexto del usuario actual.
 *   - Aplica el filtro AuthFilter para validar tokens de autenticación en cada petición.
 *   - Agrega el CurrentUserArgumentResolver para inyectar automáticamente el usuario autenticado
 *     en los controladores que lo requieran.
 *
 * Diseño:
 *   - Anotada con @Configuration para que Spring la detecte como clase de configuración.
 *   - Implementa WebMvcConfigurer para extender la configuración MVC estándar de Spring.
 *   - Define:
 *       • Un filtro de autenticación global (AuthFilter).
 *       • Un resolvedor de argumentos para el usuario actual (CurrentUserArgumentResolver).
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos resolvers o filtros, deben registrarse aquí.
 *   - Puede ampliarse con configuraciones adicionales (CORS, interceptores, etc.).
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.supervisors.config;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.proyecto.ops.supervisors.security.AuthFilter;
import com.proyecto.ops.supervisors.security.CurrentUserArgumentResolver;

/**
 * Configuración global de Spring MVC para el microservicio de técnicos.
 *
 * Registra filtros y resolvers personalizados para autenticación y contexto de usuario.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final AuthFilter authFilter;
  private final CurrentUserArgumentResolver resolver;

    /**
     * Constructor que inyecta las dependencias necesarias para registrar
     * el filtro de autenticación y el resolvedor de usuario actual.
     *
     * @param authFilter Filtro de autenticación global.
     * @param resolver   Resolvedor del usuario autenticado.
     */
  public WebConfig(AuthFilter authFilter, CurrentUserArgumentResolver resolver) {
    this.authFilter = authFilter;
    this.resolver = resolver;
  }

    /**
     * Registra el filtro de autenticación AuthFilter para todas las rutas del API.
     *
     * @return Bean de registro del filtro de autenticación.
     */
  @Bean
  public FilterRegistrationBean<AuthFilter> authFilterRegistration() {
    FilterRegistrationBean<AuthFilter> reg = new FilterRegistrationBean<>();
        // Asocia la instancia del filtro de autenticación al registro del filtro.
    reg.setFilter(authFilter);
        // Aplica el filtro a todas las rutas del microservicio.
    reg.addUrlPatterns("/*");
        // Define la prioridad de ejecución del filtro (1 = alta prioridad).
    reg.setOrder(1);
    return reg;
  }

    /**
     * Registra el CurrentUserArgumentResolver en el contexto MVC,
     * permitiendo inyectar el usuario autenticado en los controladores.
     *
     * @param resolvers Lista de resolvers registrados por Spring.
     */
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // Agrega el resolvedor personalizado al conjunto de resolvers disponibles.
    resolvers.add(resolver);
  }
}