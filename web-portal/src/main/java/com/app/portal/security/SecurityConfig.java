package com.app.portal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // habilita @PreAuthorize en tus controllers/services
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Si más adelante quieres CSRF, cambia a csrf(csrf -> csrf.ignoringRequestMatchers(...))
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // estáticos y páginas públicas
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/login", "/error").permitAll()

                // --------- Reglas por rol, alineadas al enunciado ----------
                // Administrador
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Supervisor: puede ver su tablero, planificar visitas, ver reportes del equipo
                .requestMatchers("/supervisor/**").hasAnyRole("SUPERVISOR", "ADMIN")

                // Técnicos: su tablero y visitas del día
                .requestMatchers("/tech/**").hasAnyRole("TECH", "ADMIN")

                // Visitas: GET para todos los operativos; mutaciones solo supervisor/admin
                .requestMatchers(HttpMethod.GET, "/visits/**").hasAnyRole("TECH", "SUPERVISOR", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/visits/**").hasAnyRole("SUPERVISOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/visits/**").hasAnyRole("SUPERVISOR", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/visits/**").hasRole("ADMIN")

                // Clientes: lectura general para supervisor/técnico; mutación supervisor/admin
                .requestMatchers(HttpMethod.GET, "/clients/**").hasAnyRole("TECH", "SUPERVISOR", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/clients/**").hasAnyRole("SUPERVISOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/clients/**").hasAnyRole("SUPERVISOR", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/clients/**").hasRole("ADMIN")

                // Reportes en pantalla/PDF (todos los operativos)
                .requestMatchers("/reports/**").hasAnyRole("TECH", "SUPERVISOR", "ADMIN")

                // cualquier otra cosa autenticada
                .anyRequest().authenticated()
            )

            // login basado en formulario: usamos /login (tu plantilla), y redirige a /dashboard
            .formLogin(f -> f
                .loginPage("/login")
                .loginProcessingUrl("/login") // POST del form
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll()
            )

            // logout estándar
            .logout(l -> l
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            );

        return http.build();
    }
}