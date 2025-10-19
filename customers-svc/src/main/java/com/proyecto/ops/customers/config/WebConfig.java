/*
 * -----------------------------------------------------------------------------
 * WebConfig.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Configuración global de la capa web del microservicio "customers-svc".
 *   Registra filtros y resolvers personalizados que se aplican a todas las peticiones.
 *
 * Contexto de uso:
 *   - Gestiona la autenticación y resolución de usuario actual (CurrentUser).
 *   - Permite interceptar solicitudes HTTP para validar tokens o credenciales
 *     mediante el filtro de seguridad AuthFilter.
 *   - Integra el argumento personalizado CurrentUserArgumentResolver
 *     para inyectar automáticamente el usuario autenticado en los controladores.
 *
 * Diseño:
 *   - Anotada con @Configuration para ser detectada por Spring Boot.
 *   - Implementa la interfaz WebMvcConfigurer para extender la configuración MVC.
 *   - Registra:
 *       • AuthFilter → Filtro de autenticación que protege las rutas.
 *       • CurrentUserArgumentResolver → Resolver que inyecta el usuario autenticado.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos resolvers o filtros, deben registrarse aquí.
 *   - Puede extenderse para incluir CORS, interceptores o configuraciones globales de rutas.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.customers.config;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.proyecto.ops.customers.security.AuthFilter;
import com.proyecto.ops.customers.security.CurrentUserArgumentResolver;

/**
 * Configuración global de Spring MVC para el microservicio de clientes.
 * 
 * Registra filtros y resolvers personalizados relacionados con autenticación
 * y contexto del usuario actual.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthFilter authFilter;
    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    /**
     * Constructor que inyecta las dependencias necesarias para el registro
     * de filtros y resolvers personalizados.
     *
     * @param authFilter Filtro de autenticación que protege las rutas del API.
     * @param resolver   Argument resolver que inyecta el usuario autenticado.
     */
    public WebConfig(AuthFilter authFilter, CurrentUserArgumentResolver resolver) {
        this.authFilter = authFilter;
        this.currentUserArgumentResolver = resolver;
    }

    /**
     * Registra el filtro de autenticación AuthFilter para todas las rutas del API.
     *
     * @return Bean de registro de filtro configurado para interceptar todas las peticiones HTTP.
     */
    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration() {
        FilterRegistrationBean<AuthFilter> reg = new FilterRegistrationBean<>();
        // Se asocia el filtro de autenticación al registro del filtro.
        reg.setFilter(authFilter);
        // Define el orden de ejecución del filtro (1 = alta prioridad).
        reg.setOrder(1);
        // Aplica el filtro a todas las rutas del microservicio.
        reg.addUrlPatterns("/*");
        return reg;
    }

    /**
     * Registra el resolver personalizado para inyectar el usuario autenticado
     * en los métodos de los controladores.
     *
     * @param resolvers Lista de resolvers disponibles en el contexto Spring MVC.
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // Agrega el resolver CurrentUserArgumentResolver al contexto MVC.
        resolvers.add(currentUserArgumentResolver);
    }
}